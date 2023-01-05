package deque;

public interface Deque<type> {
    void addFirst(type x);
    void addLast(type x);
    default boolean isEmpty() {
        if (this.size() != 0) {
            return false;
        }
        return true;
    }
    int size();
    void printDeque();
    type removeFirst();
    type removeLast();
    type get(int index);
}
