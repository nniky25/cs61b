package deque;
import java.util.Deque;

/** implement LinkedListDeque class
 * @author Garry
 */
public class LinkedListDeque<T> {
    private class Node {
        public Node prev;
        public T item;
        public Node next;

        public Node(Node p, T i, Node n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    private Node sentinel;
    private Node lastNode;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        lastNode = null;
        size = 0;
    }

    public void addLast(T item) {
        if (size == 0) {
            Node current = new Node(sentinel, item, sentinel);
            sentinel.next = current;
            sentinel.prev = current;
            lastNode = current;
            size = size + 1;
        } else {
            Node current = new Node(lastNode, item, sentinel);
            sentinel.prev.next = current;
            sentinel.prev = current;
            lastNode = current;
            size = size + 1;
        }
    }

    public void addFirst(T item) {
        if (size == 0) {
            addLast(item);
        } else {
            Node curFirst = sentinel.next;
            Node newFirst = new Node(sentinel, item, curFirst);
            sentinel.next = newFirst;
            size = size + 1;
        }
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        Node current;
        if (index < size / 2) {
            current = sentinel.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = sentinel.prev; // start from back
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }

        return current.item;
    }

    public T remove(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        Node current;
        T currentItem;
        if (index < size / 2) {
            current = sentinel.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = sentinel.prev;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        currentItem = current.item;
        current.prev.next = current.next;
        size = size - 1;
        return currentItem;
    }

    public T removeFirst() {
        if (size ==  0) {
            return null;
        } else {
            Node firstNode = sentinel.next;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            size = size -1;
            return firstNode.item;
        }
    }

    public T removeLast() {
        if (size ==  0) {
            return null;
        } else {
            Node lastNode = sentinel.prev;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size = size - 1;
            return lastNode.item;
        }
    }

    private T getRecursiveHelperBack(Node current, int distance) {
        if (distance == 0) {
            return current.item;
        } else {
            return getRecursiveHelperBack(current.next, distance - 1);
        }
    }

    private T getRecursiveHelperFront(Node current, int distance) {
        if (distance == 0) {
            return current.item;
        } else {
            return getRecursiveHelperFront(current.prev, distance - 1);
        }
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        if (index < size / 2) {
            return getRecursiveHelperBack(sentinel.next, index);
        } else {
            return getRecursiveHelperFront(sentinel.prev, size - index - 1);
        }
    }


    public T getFirst() {
        return sentinel.next.item;
    }

    public T getLast() {
        return lastNode.item;
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
        Node current = sentinel.next;
        for (int i = 0; i < size; i++) {
            System.out.print(current.item);
            System.out.print(" ");
            current = current.next;
            if (i == size - 1) {
                System.out.println();
            }
        }
    }

    /**@Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node current = sentinel.next;

        @Override
        public boolean hasNext() {
            return current != sentinel;
        }

        @Override
        public T next() {
            T item = current.item;
            current = current.next;
            return item;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deque)) return false;

        Deque<?> other = (Deque<?>) o;
        if (this.size() != other.size()) return false;

        Iterator<T> thisIterator = this.iterator();
        Iterator<?> otherIterator = other.iterator();

        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            T thisItem = thisIterator.next();
            Object otherItem = otherIterator.next();

            if (thisItem == null) {
                if (otherItem != null) return false;
            } else {
                if (!thisItem.equals(otherItem)) return false;
            }
        }

        return true;
    }*/
}


