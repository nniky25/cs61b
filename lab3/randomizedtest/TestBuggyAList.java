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
    public void testAddAndRemoveConsistency() {
        AListNoResizing<Integer> expectedList = new AListNoResizing<>();
        BuggyAList<Integer> actualList = new BuggyAList<>();

        final int operationsCount = 3;

        // Add elements
        for (int i = 0; i < operationsCount; i++) {
            expectedList.addLast(i);
            actualList.addLast(i);
        }

        // Remove elements and assert equality
        for (int i = 0; i < operationsCount; i++) {
            int expected = expectedList.removeLast();
            int actual = actualList.removeLast();
            assertEquals("Mismatch at remove iteration " + i, expected, actual);
        }
}

}
