"""
Modulo per la Distribuzione Spaziale del Flusso (Weighted Water-Filling & Node-Splitting).
Prototipo di Riferimento per Mod Minecraft (Java: BlockFaceAerationEngine & FlowDistributor).

Questo modulo implementa l'algoritmo che assegna in modo distribuito e proporzionale
il valore di aerazione e portata all'interno delle celle vuote dello spazio voxel:
1. Node-Splitting (u_IN -> u_OUT) per imporre la capacità volumetrica di transito di ciascuna cella (24.0 per aria libera).
2. Canali di iniezione indipendenti per ciascuna faccia aperta delle sorgenti START (emettitori puri, c(s_IN, s_OUT) = 0).
3. Weighted Water-Filling (Max-Min Fairness a Conduttanza Spaziale):
   La portata viene aumentata con scaglioni proporzionali alla vicinanza ai Goal (w_i = 1 / (1 + alpha * d_i)),
   così che i percorsi a minore resistenza fluidodinamica ricevano prioritariamente la portata.
4. Assegnazione dei flussi alle celle vuote (node_flows) e decomposizione dei cammini aumentanti (active_paths).
"""

from typing import Set, Tuple, Dict, List, Optional
import numpy as np

from constants import (
    BASE_UNIT_CAPACITY,
    GOAL_BASE_VALUE,
    INTERNAL_CAPACITY_MAP,
    DIRECTIONS_6,
    OPPOSITE_DIR,
)
from dinic_solver import DinicGraph, Edge
from bfs_explorer import get_face_open_mask

Coord3D = Tuple[int, int, int]
Direction3D = Tuple[int, int, int]


class FlowDistributor:
    """
    Gestore della Rete di Flusso e dell'Assegnazione Distribuita dell'Aerazione.
    """

    def __init__(
        self,
        grid: np.ndarray,
        sx: int,
        sy: int,
        sz: int,
        indexed_positions: List[Coord3D],
        pos_to_idx: Dict[Coord3D, int],
        starts: Set[Coord3D],
        goals: Set[Coord3D],
        start_faces: List[Tuple[Coord3D, Direction3D, Coord3D, float]],
        dist_to_goal: Dict[Coord3D, int],
        alpha: float = 0.25
    ):
        self.grid = grid
        self.sx = sx
        self.sy = sy
        self.sz = sz
        self.indexed_positions = indexed_positions
        self.pos_to_idx = pos_to_idx
        self.starts = starts
        self.goals = goals
        self.start_faces = start_faces
        self.dist_to_goal = dist_to_goal
        self.alpha = alpha

        self.N = len(indexed_positions)
        self.M = len(start_faces)
        self.SOURCE_IDX = 2 * self.N + self.M
        self.SINK_IDX = 2 * self.N + self.M + 1

        self.dinic = DinicGraph(2 * self.N + self.M + 2)
        self.source_face_edges: List[dict] = []

        # Risultati distribuiti
        self.total_max_flow: float = 0.0
        self.node_flows: Dict[Coord3D, float] = {}
        self.start_face_flows: Dict[Tuple[Coord3D, Direction3D], float] = {}
        self.start_face_caps: Dict[Tuple[Coord3D, Direction3D], float] = {}
        self.start_block_flows: Dict[Coord3D, float] = {}
        self.start_block_caps: Dict[Coord3D, float] = {}
        self.start_block_scores: Dict[Coord3D, float] = {}
        self.active_paths: List[Tuple[List[Coord3D], float]] = []

    def build_network(self):
        """
        Costruisce la rete di flusso con Node-Splitting (u_IN -> u_OUT)
        e canali di iniezione indipendenti per faccia.
        """
        # 1. Attraversamento interno del voxel (u_IN -> u_OUT)
        for i, u in enumerate(self.indexed_positions):
            u_in = 2 * i
            u_out = 2 * i + 1
            u_type = self.grid[u]

            # I blocchi START sono sorgenti pure di emissione (capacità di transito interno = 0.0)
            node_cap = 0.0 if u in self.starts else INTERNAL_CAPACITY_MAP.get(u_type, BASE_UNIT_CAPACITY)
            self.dinic.add_edge(u_in, u_out, node_cap)

            # Collegamento Sky Goal / Manual Goal -> SINK
            if u in self.goals:
                self.dinic.add_edge(u_in, self.SINK_IDX, GOAL_BASE_VALUE)

            # Archi di Adiacenza Spaziale (u_OUT -> v_IN) con capacità ridotta da bitmask
            if u not in self.starts:
                ux, uy, uz = u
                for dx, dy, dz in DIRECTIONS_6:
                    vx, vy, vz = ux + dx, uy + dy, uz + dz
                    v = (vx, vy, vz)
                    if v in self.pos_to_idx and v not in self.starts:
                        v_idx = self.pos_to_idx[v]
                        v_in = 2 * v_idx
                        v_type = self.grid[v]

                        exit_mask = get_face_open_mask(u_type, (dx, dy, dz))
                        enter_mask = get_face_open_mask(v_type, OPPOSITE_DIR[(dx, dy, dz)])
                        shared_bits = bin(exit_mask & enter_mask).count('1')

                        if shared_bits > 0:
                            edge_cap = BASE_UNIT_CAPACITY * (shared_bits / 4.0)
                            self.dinic.add_edge(u_out, v_in, edge_cap)

        # 2. Iniezione per Singola Faccia con Peso Spaziale (SOURCE -> face_node -> nxt_in)
        self.source_face_edges.clear()
        for j, (s, d, nxt, cap) in enumerate(self.start_faces):
            face_node = 2 * self.N + j
            nxt_in = 2 * self.pos_to_idx[nxt]

            # Arco da SOURCE al nodo faccia (capacità iniziale 0 per Water-Filling)
            self.dinic.add_edge(self.SOURCE_IDX, face_node, 0.0)
            # Arco dal nodo faccia all'ingresso del vicino
            self.dinic.add_edge(face_node, nxt_in, cap)

            # Calcolo peso di conduttanza spaziale: w = 1 / (1 + alpha * d)
            d_goal = self.dist_to_goal.get(nxt, 999)
            weight = (1.0 / (1.0 + self.alpha * d_goal)) if d_goal < 900 else 0.0

            # Memorizza riferimenti per il Water-Filling
            edge_ref = self.dinic.adj[self.SOURCE_IDX][-1]
            self.source_face_edges.append({
                'edge': edge_ref,
                'cap': cap,
                'start_pos': s,
                'dir': d,
                'nxt_pos': nxt,
                'face_node': face_node,
                'weight': weight,
                'd_goal': d_goal
            })

    def distribute_flow(self, delta_f: float = 0.5):
        """
        Esegue l'algoritmo di Weighted Water-Filling (Proporzionalità Spaziale e Max-Min Fairness):
        - Aumenta gradualmente la capacità concessa a ciascuna faccia con passo proporzionale a weight.
        - Spinge il flusso tramite Dinic blocking flow.
        - I percorsi più vicini ai Goal si espandono più velocemente, saturando prima i colli di bottiglia.
        - Quando una strozzatura satura, le facce non vincolate continuano a riempire la capacità residua.
        """
        while True:
            added_any_cap = False
            for item in self.source_face_edges:
                edge: Edge = item['edge']
                cap: float = item['cap']
                weight: float = item['weight']

                if edge.cap < cap:
                    step = max(0.05, delta_f * weight) if weight > 0 else 0.0
                    edge.cap = min(cap, edge.cap + step)
                    added_any_cap = True

            if not added_any_cap:
                break

            # Risoluzione Dinic sul grafo residuo corrente
            pushed_in_round = 0.0
            while self.dinic.bfs(self.SOURCE_IDX, self.SINK_IDX):
                self.dinic.ptr = [0] * self.dinic.n
                while True:
                    tr = self.dinic.dfs(self.SOURCE_IDX, self.SINK_IDX, float('inf'))
                    if tr <= 1e-6:
                        break
                    pushed_in_round += tr

        self._extract_assigned_flows()
        self._reconstruct_flow_paths()

    def _extract_assigned_flows(self):
        """Estrae i flussi calcolati su facce, blocchi e celle vuote del mondo."""
        self.start_face_flows.clear()
        self.start_face_caps.clear()
        self.start_block_flows.clear()
        self.start_block_caps.clear()
        self.start_block_scores.clear()

        for item in self.source_face_edges:
            s = item['start_pos']
            d = item['dir']
            cap = item['cap']
            f_val = item['edge'].flow

            self.start_face_flows[(s, d)] = f_val
            self.start_face_caps[(s, d)] = cap

            if s not in self.start_block_flows:
                self.start_block_flows[s] = 0.0
                self.start_block_caps[s] = 0.0

            self.start_block_flows[s] += f_val
            self.start_block_caps[s] += cap

        for s in self.starts:
            b_flow = self.start_block_flows.get(s, 0.0)
            b_cap = self.start_block_caps.get(s, 0.0)
            self.start_block_scores[s] = (b_flow / b_cap * 100.0) if b_cap > 0 else 0.0

        self.total_max_flow = sum(self.start_block_flows.values())

        # Assegnazione flussi volumetrici per singola cella vuota (u_IN -> u_OUT)
        self.node_flows.clear()
        for i, pos in enumerate(self.indexed_positions):
            u_in = 2 * i
            u_out = 2 * i + 1
            for e in self.dinic.adj[u_in]:
                if e.to == u_out:
                    if e.flow > 1e-4:
                        self.node_flows[pos] = e.flow

    def _reconstruct_flow_paths(self):
        """
        Decompone il flusso totale in singoli cammini aumentanti con flusso positivo
        a partire da ciascuna faccia di origine, per la visualizzazione 3D in GUI.
        """
        self.active_paths.clear()

        # Mappatura face_node -> (start_pos, dir, neighbor_pos)
        face_node_to_info = {}
        for item in self.source_face_edges:
            face_node_to_info[item['face_node']] = (item['start_pos'], item['dir'], item['nxt_pos'])

        # Grafo dei flussi positivi
        flow_net = {}
        for u in range(self.dinic.n):
            for e in self.dinic.adj[u]:
                if e.flow > 1e-4:
                    if u not in flow_net:
                        flow_net[u] = {}
                    flow_net[u][e.to] = e.flow

        while True:
            stack = [(self.SOURCE_IDX, [self.SOURCE_IDX], float('inf'))]
            found_path = None
            bottleneck = 0.0
            visited = set()

            while stack:
                curr, p, min_f = stack.pop()
                if curr == self.SINK_IDX:
                    found_path = p
                    bottleneck = min_f
                    break
                if curr in visited:
                    continue
                visited.add(curr)
                if curr in flow_net:
                    for nxt_node, fl in list(flow_net[curr].items()):
                        if fl > 1e-4 and nxt_node not in p:
                            stack.append((nxt_node, p + [nxt_node], min(min_f, fl)))

            if not found_path or bottleneck <= 1e-4:
                break

            for i in range(len(found_path) - 1):
                u, v = found_path[i], found_path[i + 1]
                flow_net[u][v] -= bottleneck
                if flow_net[u][v] <= 1e-4:
                    flow_net[u].pop(v, None)

            voxels = []
            for node in found_path:
                if node == self.SOURCE_IDX or node == self.SINK_IDX:
                    continue
                if node in face_node_to_info:
                    s_pos, d, nxt_pos = face_node_to_info[node]
                    if not voxels or voxels[-1] != s_pos:
                        voxels.append(s_pos)
                else:
                    if node % 2 == 0:  # u_IN
                        v_idx = node // 2
                        if v_idx < len(self.indexed_positions):
                            pos = self.indexed_positions[v_idx]
                            if not voxels or voxels[-1] != pos:
                                voxels.append(pos)

            if len(voxels) >= 1:
                self.active_paths.append((voxels, bottleneck))
