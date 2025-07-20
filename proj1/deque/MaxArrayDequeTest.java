package deque;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {

    /** Test with Integer comparator (natural order). */
    @Test
    public void testMaxWithInteger() {
        Comparator<Integer> comp = (a, b) -> a - b;
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(comp);

        mad.addLast(10);
        mad.addLast(3);
        mad.addLast(99);
        mad.addLast(42);

        assertEquals((Integer) 99, mad.max());
    }

    /** Test with empty deque. */
    @Test
    public void testMaxWithEmpty() {
        Comparator<String> comp = (a, b) -> a.length() - b.length();
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(comp);

        assertNull(mad.max());
        assertNull(mad.max(comp));
    }

    /** Test with String comparator by length. */
    @Test
    public void testMaxWithStringLength() {
        Comparator<String> comp = (a, b) -> a.length() - b.length();
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(comp);

        mad.addLast("cat");
        mad.addLast("elephant");
        mad.addLast("dog");

        assertEquals("elephant", mad.max());
    }

    /** Test with custom comparator using max(Comparator c). */
    @Test
    public void testMaxWithOverrideComparator() {
        Comparator<String> byLength = (a, b) -> a.length() - b.length();
        Comparator<String> byAlphabet = (a, b) -> a.compareTo(b);

        MaxArrayDeque<String> mad = new MaxArrayDeque<>(byLength);

        mad.addLast("cat");
        mad.addLast("elephant");
        mad.addLast("apple");

        // Using default comparator: by length
        assertEquals("elephant", mad.max());

        // Using overridden comparator: alphabetically
        assertEquals("elephant", mad.max(byAlphabet));
    }

    /** Test with only one element. */
    @Test
    public void testMaxWithSingleElement() {
        Comparator<Double> comp = (a, b) -> Double.compare(a, b);
        MaxArrayDeque<Double> mad = new MaxArrayDeque<>(comp);

        mad.addLast(3.14159);
        assertEquals((Double) 3.14159, mad.max());
    }
}