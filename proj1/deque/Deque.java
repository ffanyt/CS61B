package deque;

public interface Deque<T> {
    void addFirst(T x);
    void addLast(T x);
    default boolean isEmpty() {
        if (this.size() != 0) {
            return false;
        }
        return true;
    }
    int size();
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int index);
}
