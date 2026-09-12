"""
Modulo di Coordinamento del Mondo Voxel e del Motore di Aerazione 3D.
Prototipo di Riferimento per Mod Minecraft (Java: VoxelMaxFlowEngine / AerationManager).

Delega le responsabilità algoritmiche ai 3 moduli dedicati:
1. 'bfs_explorer.py': Esplorazione preventiva, connettività spaziale a bitmask, distanze dai Goal.
2. 'dinic_solver.py': Risolutore di Flusso Massimo (Dinic) con Level Graph BFS e Blocking Flow DFS.
3. 'flow_distributor.py': Node-Splitting volumetrico e Weighted Water-Filling a conduttanza spaziale.
"""

from typing import Set, Tuple, Dict, List, Optional
import numpy as np

from constants import (
    EMPTY,
    SOLID,
    START,
    PORTAL_12,
    PORTAL_6,
    PORTAL_3,
    PORTAL_AUTO_24,
    PORTAL_12_5,
    PORTAL_6_25,
    PORTAL_AUTO_25,
    BASE_UNIT_CAPACITY,
)
from bfs_explorer import (
    BFSExplorer,
    get_face_open_mask,
    is_passable,
    is_covered_by_ceiling as bfs_is_covered_by_ceiling,
)
from dinic_solver import DinicGraph, Edge
from flow_distributor import FlowDistributor

Coord3D = Tuple[int, int, int]
Direction3D = Tuple[int, int, int]


# =================================================================================================
# COORDINATORE MONDO VOXEL (VoxelWorld)
# =================================================================================================

class VoxelWorld:
    """
    Rete di Flusso Massimo 3D conforme al motore Java VoxelMaxFlowEngine.
    Coordina stato della griglia, caching Event-Driven in O(1) e delega
    agli algoritmi specializzati (BFSExplorer, FlowDistributor, DinicGraph).
    """

    def __init__(self, sx: int = 10, sy: int = 10, sz: int = 5):
        self.resize(sx, sy, sz)

    def resize(self, sx: int, sy: int, sz: int):
        self.sx, self.sy, self.sz = sx, sy, sz
        self.grid = np.zeros((sx, sy, sz), dtype=int)
        self.manual_goals: Set[Coord3D] = set()
        self.sky_goals: Set[Coord3D] = set()
        self.goals: Set[Coord3D] = set()
        self.portals: Dict[Coord3D, int] = {}
        self.manual_starts: Set[Coord3D] = set()
        self.starts: Set[Coord3D] = set()

        self.total_max_flow: float = 0.0
        self.total_requested: float = 0.0
        self.normalized_aeration: float = 0.0
        self.node_flows: Dict[Coord3D, float] = {}

        # Mappature per-faccia e per-blocco per GUI e calcoli
        self.start_faces: List[Tuple[Coord3D, Direction3D, Coord3D, float]] = []
        self.start_face_flows: Dict[Tuple[Coord3D, Direction3D], float] = {}
        self.start_face_caps: Dict[Tuple[Coord3D, Direction3D], float] = {}
        self.start_block_flows: Dict[Coord3D, float] = {}
        self.start_block_caps: Dict[Coord3D, float] = {}
        self.start_block_scores: Dict[Coord3D, float] = {}

        self.active_paths: List[Tuple[List[Coord3D], float]] = []
        self.visited_voxels: Set[Coord3D] = set()
        self.indexed_positions: List[Coord3D] = []
        self.pos_to_idx: Dict[Coord3D, int] = {}
        self.dist_to_goal: Dict[Coord3D, int] = {}

        self.finished: bool = False
        self.dirty: bool = True  # Flag Event-Driven per query O(1)

        self.reset_flow_network()

    def is_passable(self, pos: Coord3D) -> bool:
        """Verifica se una cella permette il passaggio d'aria (non solida né ermetica)."""
        x, y, z = pos
        if not (0 <= x < self.sx and 0 <= y < self.sy and 0 <= z < self.sz):
            return False
        return is_passable(self.grid[pos])

    def mark_dirty(self, pos: Optional[Coord3D] = None):
        """Invalida la cache locale quando viene modificato l'ambiente."""
        self.dirty = True
        self.finished = False

    def neighbor_changed(self, pos: Coord3D, neighbor_pos: Coord3D):
        """Simula l'evento di modifica vicini di blocco in Minecraft."""
        self.mark_dirty(pos)

    def is_covered_by_ceiling(self, x: int, y: int, z: int) -> bool:
        """Verifica se c'è un blocco solido/ermetico/tetto sopra (x, y, z)."""
        return bfs_is_covered_by_ceiling(self.grid, x, y, z, self.sz)

    def compute_sky_goals(self) -> Set[Coord3D]:
        """Delega a BFSExplorer il calcolo delle celle aperte al cielo sullo strato top."""
        if not hasattr(self, 'explorer'):
            self.explorer = BFSExplorer(self.grid, self.sx, self.sy, self.sz)
        return self.explorer.compute_sky_goals(self.manual_starts)

    def compute_sunlit_voxels(self) -> Set[Coord3D]:
        """Delega a BFSExplorer il calcolo dei voxel esposti al sole."""
        if not hasattr(self, 'explorer'):
            self.explorer = BFSExplorer(self.grid, self.sx, self.sy, self.sz)
        return self.explorer.compute_sunlit_voxels()

    def add_start(self, x: int, y: int, z: int):
        pos = (x, y, z)
        self.manual_goals.discard(pos)
        self.portals.pop(pos, None)
        self.manual_starts.add(pos)
        self.grid[pos] = START
        self.mark_dirty(pos)
        self.reset_flow_network()

    def remove_start(self, x: int, y: int, z: int):
        pos = (x, y, z)
        self.manual_starts.discard(pos)
        if self.grid[pos] == START:
            self.grid[pos] = EMPTY
        self.mark_dirty(pos)
        self.reset_flow_network()

    def add_goal(self, x: int, y: int, z: int):
        pos = (x, y, z)
        self.manual_starts.discard(pos)
        self.portals.pop(pos, None)
        if self.grid[pos] == SOLID:
            self.grid[pos] = EMPTY
        self.manual_goals.add(pos)
        self.mark_dirty(pos)
        self.reset_flow_network()

    def remove_goal(self, x: int, y: int, z: int):
        pos = (x, y, z)
        self.manual_goals.discard(pos)
        self.mark_dirty(pos)
        self.reset_flow_network()

    def set_block(self, x: int, y: int, z: int, b_type: int):
        pos = (x, y, z)
        self.manual_starts.discard(pos)
        self.manual_goals.discard(pos)
        self.portals.pop(pos, None)
        self.grid[pos] = b_type
        if b_type in (PORTAL_12, PORTAL_6, PORTAL_3, PORTAL_AUTO_24, PORTAL_12_5, PORTAL_6_25, PORTAL_AUTO_25):
            self.portals[pos] = b_type
        self.mark_dirty(pos)
        self.reset_flow_network()

    def clear_cell(self, x: int, y: int, z: int):
        pos = (x, y, z)
        self.manual_starts.discard(pos)
        self.manual_goals.discard(pos)
        self.portals.pop(pos, None)
        self.grid[pos] = EMPTY
        self.mark_dirty(pos)
        self.reset_flow_network()

    def reset_flow_network(self):
        """
        Costruisce la Rete di Flusso delegando a BFSExplorer e FlowDistributor.
        """
        self.explorer = BFSExplorer(self.grid, self.sx, self.sy, self.sz)
        self.sky_goals = self.explorer.compute_sky_goals(self.manual_starts)
        self.goals = self.sky_goals | self.manual_goals
        self.sunlit_voxels = self.explorer.compute_sunlit_voxels()
        self.starts = set(self.manual_starts)

        if not self.starts:
            default_start = (0, 0, 0)
            if self.is_passable(default_start):
                self.manual_starts.add(default_start)
                self.starts.add(default_start)
                self.grid[default_start] = START

        # 1. Esplorazione Preventiva BFS (bfs_explorer.py)
        self.indexed_positions, self.pos_to_idx = self.explorer.explore_reachable_space(self.starts)
        self.start_faces = self.explorer.detect_start_faces(self.starts, self.pos_to_idx)
        self.dist_to_goal = self.explorer.compute_goal_distances(self.goals, self.pos_to_idx)

        # 2. Inizializzazione FlowDistributor (flow_distributor.py)
        self.distributor = FlowDistributor(
            self.grid, self.sx, self.sy, self.sz,
            self.indexed_positions, self.pos_to_idx,
            self.starts, self.goals,
            self.start_faces, self.dist_to_goal
        )
        self.distributor.build_network()

        # Esposizione puntatori per compatibilità esterna
        self.dinic = self.distributor.dinic
        self.SOURCE_IDX = self.distributor.SOURCE_IDX
        self.SINK_IDX = self.distributor.SINK_IDX
        self.source_face_edges = self.distributor.source_face_edges
        self.total_max_flow = 0.0
        self.total_requested = 0.0
        self.normalized_aeration = 0.0
        self.node_flows = {}
        self.start_face_flows = {}
        self.start_face_caps = {}
        self.start_block_flows = {}
        self.start_block_caps = {}
        self.start_block_scores = {}
        self.active_paths = []
        self.visited_voxels = set(self.indexed_positions)
        self.finished = False

    def calculate_total_requested(self) -> float:
        """Calcola la capacità nominale erogabile da tutte le facce aperte delle sorgenti."""
        if hasattr(self, 'start_faces') and self.start_faces:
            return sum(cap for _, _, _, cap in self.start_faces)
        return 0.0

    def solve_all(self, force: bool = False):
        """
        Risolve la rete con Weighted Water-Filling (Proporzionalità Spaziale).
        Se 'dirty == False', risponde istantaneamente in O(1) da cache.
        """
        if not hasattr(self, 'distributor'):
            return

        if not self.dirty and self.finished and not force:
            return

        self.total_requested = self.calculate_total_requested()

        # Esecuzione del flusso distribuito (flow_distributor.py)
        self.distributor.distribute_flow()

        # Sincronizzazione risultati
        self.total_max_flow = self.distributor.total_max_flow
        self.normalized_aeration = (self.total_max_flow / self.total_requested) if self.total_requested > 0 else 0.0
        self.node_flows = self.distributor.node_flows
        self.start_face_flows = self.distributor.start_face_flows
        self.start_face_caps = self.distributor.start_face_caps
        self.start_block_flows = self.distributor.start_block_flows
        self.start_block_caps = self.distributor.start_block_caps
        self.start_block_scores = self.distributor.start_block_scores
        self.active_paths = self.distributor.active_paths

        self.finished = True
        self.dirty = False

    def get_start_aeration(self, pos: Coord3D) -> float:
        """Query Event-Driven O(1): Restituisce la portata totale erogata da un blocco START."""
        self.solve_all()
        return self.start_block_flows.get(pos, 0.0)

    def get_start_score_pct(self, pos: Coord3D) -> float:
        """Query Event-Driven O(1): Restituisce la percentuale di aerazione [0..100%]."""
        self.solve_all()
        return self.start_block_scores.get(pos, 0.0)

    def calculate_total_score(self) -> float:
        return self.total_max_flow


# =================================================================================================
# TRACKER EVENT-DRIVEN PER SORGENTI DINAMICHE (PLAYER / ENTITÀ IN MOVIMENTO)
# =================================================================================================

class PlayerAerationTracker:
    """
    Gestore ad alte prestazioni per calcolare l'aerazione del Player come sorgente dinamica.
    - O(1) Cache Lookup tra i tick.
    - BlockPos Integer Check: ignora micromovimenti all'interno dello stesso blocco.
    - Tick Throttling / Debounce: limita il ricalcolo a intervalli regolari (es. ogni 5 tick).
    """

    def __init__(self, world: VoxelWorld, throttle_ticks: int = 5, local_radius: int = 12):
        self.world = world
        self.throttle_ticks = throttle_ticks
        self.local_radius = local_radius
        self._entity_cache: Dict[str, dict] = {}

    def tick_player(self, player_id: str, current_pos: Coord3D, current_tick: int) -> Tuple[float, float, bool]:
        """
        Simula il PlayerTickEvent del server Minecraft.
        Ritorna: (flow_erogato, score_percentuale, ricalcolato_in_questo_tick)
        """
        state = self._entity_cache.get(player_id)

        if state is None:
            flow, pct = self._compute_aeration_at_pos(current_pos)
            self._entity_cache[player_id] = {
                'last_pos': current_pos,
                'last_tick': current_tick,
                'flow': flow,
                'pct': pct
            }
            return flow, pct, True

        pos_changed = (state['last_pos'] != current_pos)
        ticks_elapsed = current_tick - state['last_tick']

        # Stesso blocco e mondo intatto -> Riuso Cache O(1)
        if not pos_changed and not self.world.dirty:
            return state['flow'], state['pct'], False

        # Throttling / Debounce
        if pos_changed and ticks_elapsed < self.throttle_ticks and not self.world.dirty:
            return state['flow'], state['pct'], False

        # Ricalcolo
        flow, pct = self._compute_aeration_at_pos(current_pos)
        state['last_pos'] = current_pos
        state['last_tick'] = current_tick
        state['flow'] = flow
        state['pct'] = pct

        return flow, pct, True

    def _compute_aeration_at_pos(self, pos: Coord3D) -> Tuple[float, float]:
        x, y, z = pos
        orig_block = self.world.grid[x, y, z] if (0 <= x < self.world.sx and 0 <= y < self.world.sy and 0 <= z < self.world.sz) else SOLID

        if orig_block == SOLID:
            return 0.0, 0.0

        was_start = (orig_block == START)
        if not was_start:
            self.world.grid[x, y, z] = START
            self.world.manual_starts.add(pos)
            self.world.dirty = True

        self.world.solve_all()
        flow = self.world.get_start_aeration(pos)
        pct = self.world.get_start_score_pct(pos)

        if not was_start:
            self.world.grid[x, y, z] = orig_block
            self.world.manual_starts.discard(pos)
            self.world.dirty = False

        return flow, pct

    def invalidate_entity(self, player_id: str):
        if player_id in self._entity_cache:
            del self._entity_cache[player_id]

    def clear_all(self):
        self._entity_cache.clear()
