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

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int sizeL = L.size();
                int sizeB = B.size();
                assertEquals("size mismatch at position" + i, sizeL, sizeB);
            } else {
                if (L.size() > 0) {
                    int randomValue = StdRandom.uniform(0, 2);
                    if (randomValue == 0) {
                        int lastL = L.getLast();
                        int LastB = B.getLast();
                        assertEquals("getLast mismatch at position" + i, lastL, LastB);
                    } else {
                        int lastL = L.removeLast();
                        int lastB = B.removeLast();
                        assertEquals("removeLast mismatch at position" + i, lastL, lastB);
                    }
                }
            }
        }
    }
}