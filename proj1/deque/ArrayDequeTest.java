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

    @Test
    /* Test printDeque method. */
    public void PrintTest() {
        ArrayDeque<Integer> ard1 = new ArrayDeque<>();
        for (int i = 0; i < 5; i++) {
            ard1.addFirst(i);
        }

        ard1.printDeque();
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {
        ArrayDeque<String>  lld1 = new ArrayDeque<>();
        ArrayDeque<Double>  lld2 = new ArrayDeque<>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {
        ArrayDeque<Integer> ard1 = new ArrayDeque<>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ard1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ard1.removeLast());
    }

    @Test
    /* Test getFirst and getLast methods. */
    public void getFirstAndLastTest() {
        ArrayDeque<Integer> ard1 = new ArrayDeque<>();
        for (int i = 0; i < 10; i ++) {
            ard1.addLast(i);
        }

        assertEquals("lld1 first item should be 0", 0, (int) ard1.getFirst());
        assertEquals("lld1 last item should be 9", 9, (int) ard1.getLast());
    }

    @Test
    /* Test isEmpty() method. */
    public void isEmptyTest() {
        ArrayDeque<Integer> ard1 = new ArrayDeque<>();

        assertTrue(ard1.isEmpty());

        for (int i = 0; i < 10; i ++) {
            ard1.addLast(i);
            ard1.addFirst(i);
        }

        assertFalse(ard1.isEmpty());
    }

}
