"""
Modulo per l'Esplorazione Preventiva BFS e l'Analisi di Connettività Spaziale 3D.
Prototipo di Riferimento per Mod Minecraft (Java: Fast BFS / Level Scanner).

Funzionalità:
1. Geometria Direzionale a Bitmask (4 quadranti / 8 ottanti) per blocchi pieni, mezze lastre,
   scale, staccionate, botole, porte, foglie, grate e barre di ferro.
2. BFS Multi-Sorgente per identificare lo spazio aereo raggiungibile dagli emettitori.
3. Rilevamento automatico delle Facce Aperte dei blocchi START con relative capacità geometriche.
4. BFS Multi-Sorgente Inversa dai Goal/Cielo per calcolare la distanza topologica e la resistenza spaziale.
"""

from collections import deque
from typing import Set, Tuple, Dict, List, Optional
import numpy as np

from constants import (
    EMPTY,
    SOLID,
    START,
    SLAB_BOTTOM,
    SLAB_TOP,
    STAIRS_NORTH,
    STAIRS_SOUTH,
    STAIRS_WEST,
    STAIRS_EAST,
    COPPER_GRATE,
    DOOR_OPEN,
    DOOR_CLOSED,
    TRAPDOOR_OPEN,
    TRAPDOOR_CLOSED,
    FENCE,
    FENCE_GATE_OPEN,
    FENCE_GATE_CLOSED,
    WALL_CONNECTED,
    LEAVES,
    IRON_BARS,
    PORTAL_12,
    PORTAL_6,
    PORTAL_3,
    PORTAL_AUTO_24,
    PORTAL_12_5,
    PORTAL_6_25,
    PORTAL_AUTO_25,
    BASE_UNIT_CAPACITY,
    DIRECTIONS_6,
    DIR_NORTH,
    DIR_SOUTH,
    DIR_WEST,
    DIR_EAST,
    DIR_UP,
    DIR_DOWN,
    OPPOSITE_DIR,
)

Coord3D = Tuple[int, int, int]
Direction3D = Tuple[int, int, int]


# =================================================================================================
# 1. GEOMETRIA A BITMASK A 4 QUADRANTI / 8 OTTANTI
# =================================================================================================

def get_face_open_mask(block_type: int, direction: Direction3D) -> int:
    """
    Calcola la maschera a 4 bit (quadranti 0..3) aperta per la faccia specificata.
    0b1111 = 100% aperta (4 quadranti) -> 24.0
    0b0000 = 100% chiusa (ermetica)     -> 0.0
    """
    if block_type in (
        EMPTY, START, COPPER_GRATE, FENCE, IRON_BARS,
        DOOR_OPEN, TRAPDOOR_OPEN, FENCE_GATE_OPEN, FENCE_GATE_CLOSED,
        LEAVES, PORTAL_AUTO_24, PORTAL_AUTO_25
    ):
        return 0b1111

    if block_type in (SOLID, DOOR_CLOSED, TRAPDOOR_CLOSED, WALL_CONNECTED):
        return 0b0000

    if block_type in (PORTAL_12, PORTAL_12_5):
        # Metà aperta (2 quadranti su 4) -> 24 * 2/4 = 12.0
        return 0b0011

    if block_type in (PORTAL_6, PORTAL_6_25):
        # Un quarto aperto (1 quadrante su 4) -> 24 * 1/4 = 6.0
        return 0b0001

    if block_type == PORTAL_3:
        # Fessura minima (1 quadrante a banda ridotta / 1 ottante) -> 3.0
        return 0b0001

    # LASTRE (SLABS)
    if block_type == SLAB_BOTTOM:
        if direction == DIR_DOWN:
            return 0b0000  # Base solida
        if direction == DIR_UP:
            return 0b1111  # Cielo aperto sopra
        # Facce laterali: metà superiore aperta (bit 0, 1), metà inferiore solida
        return 0b0011

    if block_type == SLAB_TOP:
        if direction == DIR_UP:
            return 0b0000  # Tetto solido
        if direction == DIR_DOWN:
            return 0b1111  # Spazio aperto sotto
        # Facce laterali: metà inferiore aperta (bit 2, 3), metà superiore solida
        return 0b1100

    # SCALE (STAIRS)
    if block_type in (STAIRS_NORTH, STAIRS_SOUTH, STAIRS_WEST, STAIRS_EAST):
        solid_octants = (1 << 4) | (1 << 5) | (1 << 6) | (1 << 7)
        if block_type == STAIRS_NORTH:
            solid_octants |= (1 << 0) | (1 << 1)
        elif block_type == STAIRS_SOUTH:
            solid_octants |= (1 << 2) | (1 << 3)
        elif block_type == STAIRS_WEST:
            solid_octants |= (1 << 0) | (1 << 2)
        elif block_type == STAIRS_EAST:
            solid_octants |= (1 << 1) | (1 << 3)

        if direction == DIR_NORTH:
            octs = (0, 1, 4, 5)
        elif direction == DIR_SOUTH:
            octs = (2, 3, 6, 7)
        elif direction == DIR_WEST:
            octs = (0, 2, 4, 6)
        elif direction == DIR_EAST:
            octs = (1, 3, 5, 7)
        elif direction == DIR_UP:
            octs = (0, 1, 2, 3)
        elif direction == DIR_DOWN:
            octs = (4, 5, 6, 7)
        else:
            octs = (0, 1, 2, 3)

        mask = 0
        for i, oct_idx in enumerate(octs):
            if not (solid_octants & (1 << oct_idx)):
                mask |= (1 << i)
        return mask

    return 0b1111


def is_passable(block_type: int) -> bool:
    """Verifica se il blocco permette il passaggio d'aria (non solido e non ermetico)."""
    return block_type not in (SOLID, DOOR_CLOSED, TRAPDOOR_CLOSED, WALL_CONNECTED)


def is_covered_by_ceiling(grid: np.ndarray, x: int, y: int, z: int, sz: int) -> bool:
    """
    Verifica se la coordinata (x, y, z) è coperta da un tetto solido o ermetico.
    In Java: O(1) con level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z).
    """
    for cz in range(z + 1, sz):
        if grid[x, y, cz] in (SOLID, DOOR_CLOSED, TRAPDOOR_CLOSED, WALL_CONNECTED):
            return True
    return False


# =================================================================================================
# 2. ESPLORATORE PREVENTIVO BFS
# =================================================================================================

class BFSExplorer:
    """
    Motore di scansione preventiva dello spazio voxel:
    - Esplora il dominio raggiungibile da tutte le sorgenti attive.
    - Costruisce la mappatura degli indici compatti (pos_to_idx).
    - Rileva le facce aperte delle sorgenti con le rispettive capacità geometriche.
    - Esegue una BFS inversa multi-sorgente dai Goal per calcolare la distanza topologica e la conduttanza spaziale.
    """

    def __init__(self, grid: np.ndarray, sx: int, sy: int, sz: int):
        self.grid = grid
        self.sx = sx
        self.sy = sy
        self.sz = sz

    def explore_reachable_space(self, starts: Set[Coord3D]) -> Tuple[List[Coord3D], Dict[Coord3D, int]]:
        """
        Esegue una BFS multi-sorgente a partire dagli emettitori START per raccogliere
        tutte le celle d'aria connesse geometricamente.
        
        Ritorna:
            (indexed_positions, pos_to_idx)
        """
        indexed_positions: List[Coord3D] = []
        pos_to_idx: Dict[Coord3D, int] = {}
        queue: deque[Coord3D] = deque()

        # Inizializzazione con le sorgenti valide
        for s in sorted(starts):
            if 0 <= s[0] < self.sx and 0 <= s[1] < self.sy and 0 <= s[2] < self.sz:
                if s not in pos_to_idx:
                    idx = len(indexed_positions)
                    pos_to_idx[s] = idx
                    indexed_positions.append(s)
                    queue.append(s)

        # Propagazione BFS
        while queue:
            curr = queue.popleft()
            curr_type = self.grid[curr]
            cx, cy, cz = curr

            for dx, dy, dz in DIRECTIONS_6:
                nx, ny, nz = cx + dx, cy + dy, cz + dz
                nxt = (nx, ny, nz)
                if 0 <= nx < self.sx and 0 <= ny < self.sy and 0 <= nz < self.sz:
                    nxt_type = self.grid[nxt]
                    if not is_passable(nxt_type):
                        continue

                    # Controllo compatibilità geometrica bitmask
                    exit_mask = get_face_open_mask(curr_type, (dx, dy, dz))
                    enter_mask = get_face_open_mask(nxt_type, OPPOSITE_DIR[(dx, dy, dz)])
                    shared_bits = bin(exit_mask & enter_mask).count('1')

                    if shared_bits > 0:
                        if nxt not in pos_to_idx:
                            idx = len(indexed_positions)
                            pos_to_idx[nxt] = idx
                            indexed_positions.append(nxt)
                            queue.append(nxt)

        return indexed_positions, pos_to_idx

    def detect_start_faces(
        self,
        starts: Set[Coord3D],
        pos_to_idx: Dict[Coord3D, int]
    ) -> List[Tuple[Coord3D, Direction3D, Coord3D, float]]:
        """
        Identifica tutte le facce aperte degli emettitori START connesse allo spazio raggiungibile.
        
        Ritorna:
            Lista di tuple: (start_pos, direction, neighbor_pos, face_capacity)
        """
        start_faces: List[Tuple[Coord3D, Direction3D, Coord3D, float]] = []

        for s in sorted(starts):
            s_type = self.grid[s]
            for dx, dy, dz in DIRECTIONS_6:
                nxt = (s[0] + dx, s[1] + dy, s[2] + dz)
                if nxt in pos_to_idx:
                    exit_mask = get_face_open_mask(s_type, (dx, dy, dz))
                    enter_mask = get_face_open_mask(self.grid[nxt], OPPOSITE_DIR[(dx, dy, dz)])
                    shared_bits = bin(exit_mask & enter_mask).count('1')
                    if shared_bits > 0:
                        face_cap = BASE_UNIT_CAPACITY * (shared_bits / 4.0)
                        start_faces.append((s, (dx, dy, dz), nxt, face_cap))

        return start_faces

    def compute_goal_distances(
        self,
        goals: Set[Coord3D],
        pos_to_idx: Dict[Coord3D, int]
    ) -> Dict[Coord3D, int]:
        """
        Esegue una BFS inversa multi-sorgente da tutti i Goal (Sky Goals e manuali)
        per calcolare la distanza minima topologica d(u) di ciascun voxel verso l'uscita.
        
        Ritorna:
            dist_to_goal: mappa pos -> distanza in passi
        """
        dist_to_goal: Dict[Coord3D, int] = {}
        queue: deque[Coord3D] = deque()

        for g in goals:
            if g in pos_to_idx:
                dist_to_goal[g] = 0
                queue.append(g)

        while queue:
            curr = queue.popleft()
            curr_d = dist_to_goal[curr]
            curr_type = self.grid[curr]
            cx, cy, cz = curr

            for dx, dy, dz in DIRECTIONS_6:
                nx, ny, nz = cx + dx, cy + dy, cz + dz
                nxt = (nx, ny, nz)
                if nxt in pos_to_idx and nxt not in dist_to_goal:
                    nxt_type = self.grid[nxt]
                    if not is_passable(nxt_type):
                        continue

                    # Controllo connessione bitmask
                    exit_mask = get_face_open_mask(curr_type, (dx, dy, dz))
                    enter_mask = get_face_open_mask(nxt_type, OPPOSITE_DIR[(dx, dy, dz)])
                    shared_bits = bin(exit_mask & enter_mask).count('1')
                    if shared_bits > 0:
                        dist_to_goal[nxt] = curr_d + 1
                        queue.append(nxt)

        return dist_to_goal

    def compute_sky_goals(self, manual_starts: Set[Coord3D]) -> Set[Coord3D]:
        """
        Calcola i Goal del cielo sullo strato superiore libero (z = sz - 1).
        In Java: query istantanea O(1) con Chunk Heightmap.
        """
        sky_cells = set()
        top_z = self.sz - 1
        for x in range(self.sx):
            for y in range(self.sy):
                pos = (x, y, top_z)
                if is_passable(self.grid[pos]) and pos not in manual_starts:
                    sky_cells.add(pos)
        return sky_cells

    def compute_sunlit_voxels(self) -> Set[Coord3D]:
        """Tutti i voxel esposti alla luce del cielo (senza tetti solidi sopra)."""
        sunlit = set()
        for x in range(self.sx):
            for y in range(self.sy):
                for z in range(self.sz - 1, -1, -1):
                    if not is_passable(self.grid[x, y, z]):
                        break
                    sunlit.add((x, y, z))
        return sunlit

    @staticmethod
    def compute_conductance_weight(distance: int, alpha: float = 0.25) -> float:
        """
        Calcola il peso di conduttanza spaziale in base alla distanza dai Goal:
            w = 1 / (1 + alpha * distance)
        """
        if distance < 900:
            return 1.0 / (1.0 + alpha * distance)
        return 0.0

