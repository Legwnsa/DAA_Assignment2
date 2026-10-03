import com.example.metrics.OpCounter;
import com.example.structures.DynamicArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {
    private DynamicArray array;
    private OpCounter counter;

    @BeforeEach
    void setUp() {
        counter = new OpCounter();
        array = new DynamicArray(counter);
    }

    @Test
    void testAddAndGet() {
        array.add(10);
        array.add(20);
        assertEquals(10, array.get(0));
        assertEquals(20, array.get(1));
        assertEquals(2, array.size());
    }

    @Test
    void testAddAtIndex() {
        array.add(10);
        array.add(30);
        array.add(1, 20);
        assertEquals(10, array.get(0));
        assertEquals(20, array.get(1));
        assertEquals(30, array.get(2));
    }

    @Test
    void testRemove() {
        array.add(10);
        array.add(20);
        array.add(30);
        int removed = array.remove(1);
        assertEquals(20, removed);
        assertEquals(2, array.size());
        assertEquals(30, array.get(1));
    }

    @Test
    void testContains() {
        array.add(5);
        array.add(15);
        assertTrue(array.contains(5));
        assertTrue(array.contains(15));
        assertFalse(array.contains(99));
    }

    @Test
    void testIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(5, 10));
    }
}