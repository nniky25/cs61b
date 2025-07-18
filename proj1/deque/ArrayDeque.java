package deque;

/** implement ArrayDeque class
 * @auter Garry
 */
public class ArrayDeque<T> {
    private T[] items;
    private int firstIndex;
    private int lastIndex;
    public int length;
    private int size;

    public ArrayDeque() {
        items = (T[]) new Object[6];
        firstIndex = 0;
        lastIndex = 0;
        length = 6;
        size = 0;
    }

    public void addLast(T item) {
        if (size == 0) {
            items[lastIndex] = item;
            size = size + 1;
        } else {
            if (size == length) {
                resize(length * 4);
            }
            lastIndex = lastIndex + 1;
            items[lastIndex] = item;
            size = size + 1;
        }
    }

    public void addFirst(T item) {
        if (size == 0) {
            items[firstIndex] = item;
            size = size + 1;
        } else {
            if (size == length) {
                resize(length * 4);
            }

            if (firstIndex == 0) {
                firstIndex = length - 1;
                items[firstIndex] = item;
                size = size + 1;
            } else {
                firstIndex = firstIndex - 1;
                items[firstIndex] = item;
                size = size + 1;
            }
        }
    }

    public T get(int index) {
        if (size == 0) {
            return null;
        } else {
            if (firstIndex == 0) {
                return items[index];
            } else {
                return items[(index + firstIndex) % length];
            }
        }
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        if (items[0] != null) {
            System.arraycopy(items, 0, a, 0, lastIndex + 1);
            if (firstIndex != 0) {
                if (length < capacity) {
                    System.arraycopy(items, lastIndex + 1, a, capacity - (size - firstIndex), size - firstIndex);
                    firstIndex = capacity - (size - firstIndex);
                } else {
                    System.arraycopy(items, firstIndex, a, capacity - (length - firstIndex), length - firstIndex);
                    firstIndex = capacity - (length - firstIndex);
                }
            }
        } else {
            System.arraycopy(items, firstIndex, a, 0, lastIndex - firstIndex + 1);
            firstIndex = 0;
            lastIndex = firstIndex + size -1;
        }
        items = a;
        length = capacity;
    }

    public T removeFirst() {
        T returnItem = items[firstIndex];
        if (size == 0) {
            return null;
        } else {
            if (length >= 6 * 4) {
                if ((size - 1) * 4 < length) {
                    resize(length / 2);
                }
            }
            size = size - 1;
            items[firstIndex] = null;

            if (size == 0) {
                lastIndex = 0;
                firstIndex = 0;
            } else {
                if (firstIndex == length - 1) {
                    firstIndex = 0;
                } else {
                    firstIndex = firstIndex + 1;
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
            if (length >= 6 * 4) {
                if ((size - 1) * 4 < length) {
                    resize(length / 2);
                }
            }
            size = size - 1;
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

    public void printDeque() {
        if (firstIndex <= lastIndex) {
            int index = firstIndex;
            for (int i = 0; i < size; i++) {
                System.out.print(items[index]);
                System.out.print(" ");
            }
            System.out.println();
        } else {
            for (int i = firstIndex; i < length; i++) {
                System.out.print(items[i]);
                System.out.print(" ");
            }
            for (int i = 0; i <= lastIndex; i++) {
                System.out.print(items[i]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public T getFirst() {
        if (size == 0) {
            return null;
        }

        return items[firstIndex];
    }

    public T getLast() {
        if (size == 0) {
            return null;
        }

        return items[lastIndex];
    }

    /**@Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            int realIndex = (firstIndex + 1 + index) % length;
            T item = items[realIndex];
            index++;
            return item;
        }
    }*/
}