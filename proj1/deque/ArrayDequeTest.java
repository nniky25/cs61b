package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void test() {
        ArrayDeque<Integer> ard1 = new ArrayDeque<>();
        for (int i = 0; i < 1000; i++) {
            ard1.addLast(i);
        }
        System.out.println((int) ard1.get(5));
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigArDequeTest() {
        ArrayDeque<Integer> ard1 = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            ard1.addLast(i);
        }

        for (int i = 0; i < 500000; i++) {
            assertEquals("Should have the same value" + i, i, (int) ard1.removeFirst());
        }

        for (int i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value" + i, i, (int) ard1.removeLast());
        }
    }
}
