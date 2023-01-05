package deque;
import java.util.Iterator;
public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    public class IntNode {
        T item;
        IntNode prev;
        IntNode next;
        private IntNode(T x, IntNode n) {
            item = x;
            next = n;
        }
    }
    private IntNode sentinel;
    private int size;
    public LinkedListDeque() {
        IntNode p = new IntNode(null, null);
        sentinel = p;
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }
    @Override
    public void addFirst(T x) {
        IntNode p = new IntNode(x, sentinel.next);
        sentinel.next.prev = p;
        p.prev = sentinel;
        sentinel.next = p;
        size += 1;
    }
    @Override
    public void addLast(T x) {
        IntNode p = new IntNode(x, sentinel);
        p.prev = sentinel.prev;
        sentinel.prev.next = p;
        sentinel.prev = p;
        size += 1;
    }
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        IntNode p = sentinel.next;
        while (p != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        T temp = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return temp;
    }
    @Override
    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        T temp = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size -= 1;
        return temp;
    }
    @Override
    public T get(int index) {
        if (this.isEmpty()) {
            return null;
        }
        IntNode p = sentinel.next;
        while (index != 0) {
            p = p.next;
            index -= 1;
        }
        T k = p.item;
        return p.item;
    }
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }
    private T getRecursiveHelper(int index, IntNode n) {
        if (index == 0) {
            return n.item;
        }
        return getRecursiveHelper(index - 1, n.next);
    }
    public Iterator<T> iterator() {
        return new LlIterator();
    }
    private class LlIterator implements Iterator {
        private IntNode p;
        private LlIterator() {
            p = sentinel.next;
        }
        public boolean hasNext() {
            return p != sentinel;
        }
        public T next() {
            T n = p.item;
            p = p.next;
            return n;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class a = o.getClass();
        Class b = this.getClass();
        Class c = ArrayDeque.class;
        if ((a != b) && (a != c)) {
            return false;
        }
        if (a == b) {
            LinkedListDeque p = (LinkedListDeque) o;
            if (p.size() != size) {
                return false;
            }
            int j = 0;
            for (T i : this) {
                if (!i.equals(p.get(j))) {
                    return false;
                }
                j += 1;
            }
        } else {
            ArrayDeque p = (ArrayDeque) o;
            if (p.size() != size) {
                return false;
            }
            int j = 0;
            for (T i : this) {
                if (!i.equals(p.get(j))) {
                    return false;
                }
                j += 1;
            }
        }
        return true;
    }
}
