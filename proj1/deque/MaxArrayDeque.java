package deque;
import java.util.Comparator;

/** extends ArrayDeque to MaxArrayDeque.
 * @auter Garry
 */
public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    public T max() {
        if (isEmpty()) return null;

        T maxItem = get(0);
        for (int i = 1; i < size(); i++) {
            T current = get(i);
            if (comparator.compare(current, maxItem) > 0) {
                maxItem = current;
            }
        }
        return maxItem;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        T maxItem = get(0);
        for (int i = 1; i < size(); i++) {
            T currentItem = get(i);
            if (c.compare(currentItem, maxItem) > 0) {
                maxItem = currentItem;
            }
        }
        return maxItem;
    }
}