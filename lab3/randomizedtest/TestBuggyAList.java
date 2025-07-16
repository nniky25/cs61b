package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> testList1 = new AListNoResizing<>();
        BuggyAList<Integer> testList2 = new BuggyAList<>();

        int addTimes = 3;
        int removeTimes = 3;

        for (int i = 0; i < addTimes; i++) {
            testList1.addLast(i);
            testList2.addLast(i);
        }
        for (int i = 0; i < removeTimes; i++) {
            int a = testList1.removeLast();
            int b = testList2.removeLast();
            assertEquals(a, b);
        }
    }

}
