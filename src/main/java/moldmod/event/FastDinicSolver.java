package moldmod.event;

import java.util.Arrays;

/**
 * High-performance Zero-GC Flat Array Dinic Max-Flow Solver.
 * Faithful Java 1:1 translation of Test/dinic_solver.py.
 *
 * Implements Level Graph BFS and Blocking Flow DFS with residual pointers.
 */
public class FastDinicSolver {
    public final int n;
    public final int[] head;
    public final int[] to;
    public final int[] next;
    public final double[] cap;
    public final double[] flow;
    public final int[] level;
    public final int[] ptr;
    public final int[] queue;
    public int edgeCount;

    public FastDinicSolver(int n, int maxEdges) {
        this.n = n;
        this.head = new int[n];
        Arrays.fill(this.head, -1);
        int doubleEdges = Math.max(8, maxEdges * 2);
        this.to = new int[doubleEdges];
        this.next = new int[doubleEdges];
        this.cap = new double[doubleEdges];
        this.flow = new double[doubleEdges];
        this.level = new int[n];
        this.ptr = new int[n];
        this.queue = new int[n];
        this.edgeCount = 0;
    }

    public void reset(int nodeCount) {
        Arrays.fill(this.head, 0, nodeCount, -1);
        this.edgeCount = 0;
    }

    public int addEdge(int u, int v, double capacity) {
        if (u < 0 || u >= n || v < 0 || v >= n || capacity < 0.0) {
            return -1;
        }
        int e1 = edgeCount++;
        to[e1] = v;
        cap[e1] = capacity;
        flow[e1] = 0.0;
        next[e1] = head[u];
        head[u] = e1;

        int e2 = edgeCount++;
        to[e2] = u;
        cap[e2] = 0.0;
        flow[e2] = 0.0;
        next[e2] = head[v];
        head[v] = e2;

        return e1;
    }

    public boolean bfs(int src, int sink) {
        Arrays.fill(level, -1);
        level[src] = 0;
        int qHead = 0;
        int qTail = 0;
        queue[qTail++] = src;

        while (qHead < qTail) {
            int u = queue[qHead++];
            for (int e = head[u]; e != -1; e = next[e]) {
                int v = to[e];
                if (cap[e] - flow[e] > 1e-6 && level[v] < 0) {
                    level[v] = level[u] + 1;
                    queue[qTail++] = v;
                }
            }
        }
        return level[sink] >= 0;
    }

    public double dfs(int u, int sink, double pushed) {
        if (u == sink || pushed <= 1e-6) {
            return pushed;
        }

        for (int e = ptr[u]; e != -1; e = next[e]) {
            ptr[u] = e;
            int trg = to[e];
            if (level[trg] != level[u] + 1 || cap[e] - flow[e] <= 1e-6) {
                continue;
            }
            double tr = dfs(trg, sink, Math.min(pushed, cap[e] - flow[e]));
            if (tr > 1e-6) {
                flow[e] += tr;
                flow[e ^ 1] -= tr;
                return tr;
            }
        }
        return 0.0;
    }

    public double maxFlow(int src, int sink) {
        double totalFlow = 0.0;
        while (bfs(src, sink)) {
            System.arraycopy(head, 0, ptr, 0, n);
            while (true) {
                double tr = dfs(src, sink, Double.POSITIVE_INFINITY);
                if (tr <= 1e-6) {
                    break;
                }
                totalFlow += tr;
            }
        }
        return totalFlow;
    }

    public void resetFlows() {
        Arrays.fill(flow, 0.0);
    }

    public double getEdgeFlow(int edgeIdx) {
        return (edgeIdx >= 0 && edgeIdx < edgeCount) ? flow[edgeIdx] : 0.0;
    }

    public double getEdgeCap(int edgeIdx) {
        return (edgeIdx >= 0 && edgeIdx < edgeCount) ? cap[edgeIdx] : 0.0;
    }

    public void setEdgeCap(int edgeIdx, double capacity) {
        if (edgeIdx >= 0 && edgeIdx < edgeCount) {
            cap[edgeIdx] = capacity;
        }
    }
}
