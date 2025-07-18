package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void test() {
        ArrayDeque<Integer> ard1 = new ArrayDeque<>();
        ard1.addLast(1);
        System.out.println((int) ard1.get(5));
    }
}
