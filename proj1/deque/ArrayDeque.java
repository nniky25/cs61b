package deque;

/** implement ArrayDeque class
 * @auter Garry
 */
public class ArrayDeque<T> {
    private T[] items;
    private int firstIndex;
    private int lastIndex;
    private int length;
    private int size;

    public ArrayDeque() {
        items = (T[]) new Object[6];
        firstIndex = 0;
        lastIndex = 0;
        length = 6;
        size = 0;
    }

    public void addLast(T item) {
        if (size == length) {
            resize(length * 4);
        }
        lastIndex = lastIndex + 1;
        items[lastIndex] = item;
        size = size + 1;
    }

    public void addFirst(T item) {
        if (size == length) {
            resize(length * 4);
        }
        if (firstIndex == 0) {
            firstIndex = length - 1;
            items[firstIndex] = item;
        } else {
            firstIndex = firstIndex - 1;
            items[firstIndex] = item;
        }

        size = size + 1;
    }

    public T get(int index) {
        if (size == 0) {
            if (firstIndex == 0) {
                return items[index];
            } else {
                return items[(index + firstIndex) % length];
            }
        } else {
            return null;
        }
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        if (items[0] != null) {
            System.arraycopy(items, 0, a, 0, lastIndex + 1);
            if (firstIndex != 0) {
                if (size < capacity) {
                    System.arraycopy(items, lastIndex + 1, a, capacity - (size - firstIndex), size - firstIndex);
                    firstIndex = capacity - (size - firstIndex);
                } else {
                    System.arraycopy(items, capacity - (size - firstIndex), a, capacity - (size - firstIndex), size - firstIndex);
                    firstIndex = capacity - (size - firstIndex);
                }
            }
        } else {
            System.arraycopy(items, firstIndex, a, firstIndex, lastIndex - firstIndex + 1);
        }
        items = a;
        length = capacity;
    }

    public T removeFirst() {
        T returnItem = items[firstIndex];
        if (size == 0) {
            return null;
        } else {
            size = size - 1;
            if (length >= 6 * 4) {
                if (size * 4 < length) {
                    resize(length / 4);
                }
            }

            items[firstIndex] = null;

            if (size == 0) {
                lastIndex = 0;
                firstIndex = 0;
            } else {
                if (firstIndex == length) {
                    firstIndex = 0;
                } else {
                    firstIndex = firstIndex - 1;
                }
            }
        }
        return returnItem;
    }

    public T removeLast() {
        T returnItem = items[lastIndex];
        if (size == 0) {
            return null;
        } else {
            size = size - 1;
            if (length >= 6 * 4) {
                if (size * 4 < length) {
                    resize(length / 4);
                }
            }

            items[lastIndex] = null;

            if (size == 0) {
                lastIndex = 0;
                firstIndex = 0;
            } else {
                if (lastIndex == 0) {
                    lastIndex = length - 1;
                } else {
                    lastIndex = lastIndex - 1;
                }
            }
        }
        return returnItem;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }
}