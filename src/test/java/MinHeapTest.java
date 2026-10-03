import com.example.metrics.OpCounter;
import com.example.structures.MinHeap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {
    private MinHeap heap;
    private OpCounter counter;

    @BeforeEach
    void setUp() {
        counter = new OpCounter();
        heap = new MinHeap(counter);
    }

    @Test
    void testInsertAndExtractMin() {
        heap.insert(15);
        heap.insert(10);
        heap.insert(20);
        heap.insert(5);

        assertEquals(5, heap.peekMin());
        assertEquals(5, heap.extractMin());
        assertEquals(10, heap.extractMin());
        assertEquals(15, heap.extractMin());
        assertEquals(20, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testSortedOutputOnRandomData() {
        java.util.Random rnd = new java.util.Random(42);
        int n = 100;
        int[] input = new int[n];
        for (int i = 0; i < n; i++) {
            input[i] = rnd.nextInt(1000);
            heap.insert(input[i]);
        }

        int prev = heap.extractMin();
        for (int i = 1; i < n; i++) {
            int curr = heap.extractMin();
            assertTrue(prev <= curr, "Heap property violated: " + prev + " > " + curr);
            prev = curr;
        }
    }

    @Test
    void testExceptions() {
        assertThrows(IllegalStateException.class, () -> heap.peekMin());
        assertThrows(IllegalStateException.class, () -> heap.extractMin());
    }
}