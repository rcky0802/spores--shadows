"""
Modulo Risolutore di Flusso Massimo: Dinic (Level Graph BFS + Blocking Flow DFS).
Prototipo di Riferimento per Mod Minecraft (Java: Zero-GC FastDinicSolver).

===================================================================================================
NOTE DI TRADUZIONE IN JAVA PER MINECRAFT (Zero-GC Optimization):
===================================================================================================
In Java per Minecraft, l'allocazione continua di oggetti (es. new Edge(), ArrayList) durante i tick
causa pause per Garbage Collection che riducono i TPS.
Per raggiungere prestazioni sub-millisecondo (< 0.5 ms):
- Usare una struttura 'Flat Array' statica con array primitivi:
  public class FastDinicSolver {
      private final int[] head, to, next, level, ptr, queue;
      private final float[] cap, flow;
      private int edgeCount;
      
      public void reset(int nodeCount) {
          Arrays.fill(head, 0, nodeCount, -1);
          edgeCount = 0;
      }
      
      public void addEdge(int u, int v, float capacity) {
          to[edgeCount] = v; cap[edgeCount] = capacity; flow[edgeCount] = 0; next[edgeCount] = head[u]; head[u] = edgeCount++;
          to[edgeCount] = u; cap[edgeCount] = 0; flow[edgeCount] = 0; next[edgeCount] = head[v]; head[v] = edgeCount++;
      }
  }
===================================================================================================
"""

from collections import deque
from typing import List


class Edge:
    """Rappresenta un arco orientato con capacità residua e puntatore all'arco inverso."""
    __slots__ = ('to', 'rev', 'cap', 'flow')

    def __init__(self, to: int, rev: int, cap: float):
        self.to: int = to
        self.rev: int = rev
        self.cap: float = cap
        self.flow: float = 0.0


class DinicGraph:
    """
    Rete di flusso e risolutore di Flusso Massimo (Dinic).
    Complessità teorica: O(V^2 * E).
    Su grafi unitari / mesh spaziali con vincoli di capacità: O(E * sqrt(V)) -> tempo reale < 0.5ms.
    """

    def __init__(self, n: int):
        self.n: int = n
        self.adj: List[List[Edge]] = [[] for _ in range(n)]
        self.level: List[int] = [-1] * n
        self.ptr: List[int] = [0] * n

    def add_edge(self, u: int, v: int, cap: float):
        """Aggiunge un arco orientato u -> v con capacità 'cap' e arco residuo v -> u con capacità 0."""
        if u < 0 or u >= self.n or v < 0 or v >= self.n or cap < 0.0:
            return
        a = Edge(v, len(self.adj[v]), cap)
        b = Edge(u, len(self.adj[u]), 0.0)
        self.adj[u].append(a)
        self.adj[v].append(b)

    def bfs(self, src: int, sink: int) -> bool:
        """
        Costruisce il Level Graph determinando le distanze minime da 'src' a tutti i nodi.
        Ritorna True se 'sink' è raggiungibile.
        """
        for i in range(self.n):
            self.level[i] = -1
        self.level[src] = 0
        queue = deque([src])

        while queue:
            u = queue.popleft()
            for e in self.adj[u]:
                if e.cap - e.flow > 1e-6 and self.level[e.to] < 0:
                    self.level[e.to] = self.level[u] + 1
                    queue.append(e.to)

        return self.level[sink] >= 0

    def dfs(self, u: int, sink: int, pushed: float) -> float:
        """
        Spinge il flusso lungo il Level Graph fino a trovare un cammino bloccante.
        Usa puntatori residui 'ptr' per evitare di rivalutare archi saturi.
        """
        if u == sink or pushed <= 1e-6:
            return pushed

        for cid in range(self.ptr[u], len(self.adj[u])):
            self.ptr[u] = cid
            e = self.adj[u][cid]
            tr = e.to

            if self.level[tr] != self.level[u] + 1 or e.cap - e.flow <= 1e-6:
                continue

            pushable = min(pushed, e.cap - e.flow)
            tr_pushed = self.dfs(tr, sink, pushable)
            if tr_pushed > 1e-6:
                e.flow += tr_pushed
                self.adj[tr][e.rev].flow -= tr_pushed
                return tr_pushed

        return 0.0

    def max_flow(self, src: int, sink: int) -> float:
        """Esegue l'algoritmo di Dinic standard fino a saturazione totale del flusso."""
        total_flow = 0.0
        while self.bfs(src, sink):
            self.ptr = [0] * self.n
            while True:
                pushed = self.dfs(src, sink, float('inf'))
                if pushed <= 1e-6:
                    break
                total_flow += pushed
        return total_flow

    def reset_flows(self):
        """Azzera tutti i flussi preservando le capacità degli archi."""
        for u in range(self.n):
            for e in self.adj[u]:
                e.flow = 0.0
