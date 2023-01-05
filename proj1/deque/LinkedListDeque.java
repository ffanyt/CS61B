package deque;
import java.util.Iterator;
public class LinkedListDeque<type> implements Iterable<type>,Deque<type> {
    public class IntNode{
        type item;
        IntNode prev;
        IntNode next;
        public IntNode(type x, IntNode n) {
            item = x;
            next = n;
        }
    }
    private IntNode sentinel;
    private int size;
    public LinkedListDeque() {
        IntNode p = new IntNode(null,null);
        sentinel = p;
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }
    @Override
    public void addFirst(type x) {
        IntNode p = new IntNode(x,sentinel.next);
        sentinel.next.prev = p;
        p.prev = sentinel;
        sentinel.next = p;
        size += 1;
    }
    @Override
    public void addLast(type x) {
        IntNode p = new IntNode(x,sentinel);
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
    public type removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        type temp = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return temp;
    }
    @Override
    public type removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        type temp = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size -= 1;
        return temp;
    }
    @Override
    public type get(int index) {
        if (this.isEmpty()) {
            return null;
        }
        IntNode p = sentinel.next;
        while (index != 0) {
            p = p.next;
            index -= 1;
        }
        return p.item;
    }
    public type getRecursive(int index) {
        if (this.isEmpty()) {
            return null;
        }
        IntNode p = sentinel.next;
        while (index != 0) {
            p = p.next;
            index -= 1;
        }
        return p.item;
    }
    public Iterator<type> iterator() {
        return new LL_Iterator();
    }
    private class LL_Iterator implements Iterator {
        IntNode p;
        public LL_Iterator() {
            p = sentinel.next;
        }
        public boolean hasNext() {
            return p != sentinel;
        }
        public type next() {
            type n = p.item;
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
        if (o.getClass() != this.getClass()) {
            return false;
        }
        LinkedListDeque p = (LinkedListDeque) o;
        if (p.size() != size) {
            return false;
        }
        int j = 0;
        for (type i : this) {
            if (i != p.get(j)) {
                return false;
            }
            j += 1;
        }
        return true;
    }
}
