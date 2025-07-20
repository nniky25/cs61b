package deque;

public interface Deque<T> {
    public void addFirst(T item);
    public void addLast(T item);
    public T getFirst();
    public T getLast();
    public void printDeque();
    public T removeFirst();
    public T removeLast();
    public T get(int index);
    public int size();
    default public boolean isEmpty() {
        if (this.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
