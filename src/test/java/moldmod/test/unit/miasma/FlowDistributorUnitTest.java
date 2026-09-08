package moldmod.test.unit.miasma;

import moldmod.event.FastDinicSolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FlowDistributorUnitTest {

    @Test
    @DisplayName("FastDinic: Simple 2-hop path respects minimum bottleneck capacity")
    public void testFastDinicBottleneck() {
        // Nodes: 0 (Source) -> 1 (Intermediate) -> 2 (Sink)
        FastDinicSolver dinic = new FastDinicSolver(3, 4);
        int e1 = dinic.addEdge(0, 1, 24.0);
        int e2 = dinic.addEdge(1, 2, 6.0); // bottleneck

        double totalFlow = 0.0;
        while (dinic.bfs(0, 2)) {
            System.arraycopy(dinic.head, 0, dinic.ptr, 0, dinic.n);
            while (true) {
                double pushed = dinic.dfs(0, 2, Double.POSITIVE_INFINITY);
                if (pushed <= 1e-6) break;
                totalFlow += pushed;
            }
        }

        assertEquals(6.0, totalFlow, 1e-6, "Total max flow must be bounded by the 6.0 bottleneck");
        assertEquals(6.0, dinic.getEdgeFlow(e1), 1e-6);
        assertEquals(6.0, dinic.getEdgeFlow(e2), 1e-6);
    }

    @Test
    @DisplayName("FastDinic: Parallel independent paths sum correctly")
    public void testFastDinicParallelPaths() {
        // Source (0) -> A (1) -> Sink (3) [Cap: 12.0]
        // Source (0) -> B (2) -> Sink (3) [Cap: 18.0]
        FastDinicSolver dinic = new FastDinicSolver(4, 8);
        dinic.addEdge(0, 1, 12.0);
        dinic.addEdge(1, 3, 12.0);
        dinic.addEdge(0, 2, 18.0);
        dinic.addEdge(2, 3, 18.0);

        double totalFlow = 0.0;
        while (dinic.bfs(0, 3)) {
            System.arraycopy(dinic.head, 0, dinic.ptr, 0, dinic.n);
            while (true) {
                double pushed = dinic.dfs(0, 3, Double.POSITIVE_INFINITY);
                if (pushed <= 1e-6) break;
                totalFlow += pushed;
            }
        }

        assertEquals(30.0, totalFlow, 1e-6, "Total max flow through parallel branches must be 12.0 + 18.0 = 30.0");
    }

    @Test
    @DisplayName("FastDinic: Diamond network with central bottleneck")
    public void testFastDinicDiamondBottleneck() {
        // Source(0) -> A(1) [24.0], Source(0) -> B(2) [24.0]
        // A(1) -> C(3) [10.0], B(2) -> C(3) [10.0]
        // C(3) -> Sink(4) [15.0 bottleneck]
        FastDinicSolver dinic = new FastDinicSolver(5, 10);
        dinic.addEdge(0, 1, 24.0);
        dinic.addEdge(0, 2, 24.0);
        dinic.addEdge(1, 3, 10.0);
        dinic.addEdge(2, 3, 10.0);
        int exitEdge = dinic.addEdge(3, 4, 15.0);

        double totalFlow = 0.0;
        while (dinic.bfs(0, 4)) {
            System.arraycopy(dinic.head, 0, dinic.ptr, 0, dinic.n);
            while (true) {
                double pushed = dinic.dfs(0, 4, Double.POSITIVE_INFINITY);
                if (pushed <= 1e-6) break;
                totalFlow += pushed;
            }
        }

        assertEquals(15.0, totalFlow, 1e-6, "Flow must be throttled to 15.0 by the C->Sink edge");
        assertEquals(15.0, dinic.getEdgeFlow(exitEdge), 1e-6);
    }

    @Test
    @DisplayName("Node-Splitting: Intermediate node capacity limits throughput")
    public void testNodeSplittingCapacity() {
        // Node 1 is split into 1_IN (node 1) and 1_OUT (node 2) with capacity 12.0 (e.g. Slab)
        // Source (0) -> 1_IN (1) [Cap: 24.0]
        // 1_IN (1) -> 1_OUT (2) [Internal Cap: 12.0]
        // 1_OUT (2) -> Sink (3) [Cap: 24.0]
        FastDinicSolver dinic = new FastDinicSolver(4, 6);
        dinic.addEdge(0, 1, 24.0);
        int internalEdge = dinic.addEdge(1, 2, 12.0);
        dinic.addEdge(2, 3, 24.0);

        double totalFlow = 0.0;
        while (dinic.bfs(0, 3)) {
            System.arraycopy(dinic.head, 0, dinic.ptr, 0, dinic.n);
            while (true) {
                double pushed = dinic.dfs(0, 3, Double.POSITIVE_INFINITY);
                if (pushed <= 1e-6) break;
                totalFlow += pushed;
            }
        }

        assertEquals(12.0, totalFlow, 1e-6, "Node internal splitting must constrain flow to 12.0");
        assertEquals(12.0, dinic.getEdgeFlow(internalEdge), 1e-6);
    }

    @Test
    @DisplayName("Conservation of Flow: Flow entering intermediate nodes equals flow leaving")
    public void testConservationOfFlow() {
        FastDinicSolver dinic = new FastDinicSolver(6, 12);
        // Source(0) -> A(1) -> C(3) -> Sink(5)
        // Source(0) -> B(2) -> D(4) -> Sink(5)
        // Cross edge: A(1) -> D(4) [Cap: 5.0]
        dinic.addEdge(0, 1, 10.0);
        dinic.addEdge(0, 2, 10.0);
        dinic.addEdge(1, 3, 8.0);
        dinic.addEdge(1, 4, 5.0);
        dinic.addEdge(2, 4, 8.0);
        dinic.addEdge(3, 5, 20.0);
        dinic.addEdge(4, 5, 20.0);

        double totalFlow = 0.0;
        while (dinic.bfs(0, 5)) {
            System.arraycopy(dinic.head, 0, dinic.ptr, 0, dinic.n);
            while (true) {
                double pushed = dinic.dfs(0, 5, Double.POSITIVE_INFINITY);
                if (pushed <= 1e-6) break;
                totalFlow += pushed;
            }
        }
        assertEquals(18.0, totalFlow, 1e-6, "Total flow pushed should equal 18.0");

        // Verify net flow for node A (1): Flow In == Flow Out
        double flowInA = 0.0;
        double flowOutA = 0.0;
        for (int u = 0; u < 6; u++) {
            for (int e = dinic.head[u]; e != -1; e = dinic.next[e]) {
                if (dinic.to[e] == 1 && dinic.cap[e] > 0) flowInA += dinic.flow[e];
                if (u == 1 && dinic.cap[e] > 0) flowOutA += dinic.flow[e];
            }
        }
        assertEquals(flowInA, flowOutA, 1e-6, "Flow conservation must hold at intermediate node A");
    }

    @Test
    @DisplayName("Water-Filling: Dynamic incremental capacity injection")
    public void testWaterFillingIncrementalInjection() {
        // Source (0) -> Channel A (1) [Weight: 1.0] -> Sink (3) [Cap: 20.0]
        // Source (0) -> Channel B (2) [Weight: 0.5] -> Sink (3) [Cap: 20.0]
        FastDinicSolver dinic = new FastDinicSolver(4, 8);
        int srcA = dinic.addEdge(0, 1, 0.0); // dynamic source edge
        int srcB = dinic.addEdge(0, 2, 0.0); // dynamic source edge
        dinic.addEdge(1, 3, 20.0);
        dinic.addEdge(2, 3, 20.0);

        double weightA = 1.0;
        double weightB = 0.5;
        double deltaF = 2.0;

        // Step 1: Inject flow proportional to weights
        dinic.setEdgeCap(srcA, deltaF * weightA); // 2.0
        dinic.setEdgeCap(srcB, deltaF * weightB); // 1.0

        double step1Flow = 0.0;
        while (dinic.bfs(0, 3)) {
            System.arraycopy(dinic.head, 0, dinic.ptr, 0, dinic.n);
            while (true) {
                double pushed = dinic.dfs(0, 3, Double.POSITIVE_INFINITY);
                if (pushed <= 1e-6) break;
                step1Flow += pushed;
            }
        }

        assertEquals(3.0, step1Flow, 1e-6, "Step 1 pushed flow must be 2.0 + 1.0 = 3.0");
        assertEquals(2.0, dinic.getEdgeFlow(srcA), 1e-6);
        assertEquals(1.0, dinic.getEdgeFlow(srcB), 1e-6);
    }
}
