package deque;

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
            sentinel.prev = current;
            lastNode = current;
            size = size + 1;
        }
    }

    public void addFirst(T item) {
        if (size == 0) {
            addLast(item);
        } else {
            Node curFirstNode = sentinel.next;
            Node newFirstNode = new Node(sentinel, item, curFirstNode);
            sentinel.next = newFirstNode;
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
            return getRecursiveHelperFront(sentinel.prev, size - index);
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
}


