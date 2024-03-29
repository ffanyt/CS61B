package deque;
import java.util.Iterator;
public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] item;
    private int size;
    private int nextFirst;
    private int nextLast;
    public ArrayDeque() {
        item = (T []) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 4;
    }
    private int findbefore(int pre) {
        int current = pre - 1;
        if (pre == 0) {
            current = item.length - 1;
        }
        return current;
    }
    private int findnext(int pre) {
        int current = pre + 1;
        if (pre == item.length - 1) {
            current = 0;
        }
        return current;
    }
    private void resize(int s) {
        T[] temp = (T []) new Object[s];
        int currentfirst = findnext(nextFirst);
        int cunrrentlast = findbefore(nextLast);
        if (s > item.length) {
            if (currentfirst <= cunrrentlast) {
                for (int i = currentfirst; i <= cunrrentlast; i++) {
                    temp[i] = item[i];
                }
                item = temp;
                nextFirst = findbefore(currentfirst);
                nextLast = findnext(cunrrentlast);

            } else {
                int j = 0;
                for (int i = currentfirst; i < item.length; i++) {
                    temp[j] = item[i];
                    j += 1;
                }
                for (int i = 0; i <= cunrrentlast; i++) {
                    temp[j] = item[i];
                    j += 1;
                }
                nextFirst = temp.length - 1;
                nextLast = j;
            }
        } else {
            if (currentfirst <= cunrrentlast) {
                int j = 0;
                for (int i = currentfirst; i <= cunrrentlast; i++) {
                    temp[j] = item[i];
                    j += 1;
                }
                nextLast = j;
                nextFirst = temp.length - 1;
            } else {
                int j = 0;
                for (int i = currentfirst; i < item.length; i++) {
                    temp[j] = item[i];
                    j += 1;
                }
                for (int i = 0; i <= cunrrentlast; i++) {
                    temp[j] = item[i];
                    j += 1;
                }
                nextLast = j;
                nextFirst = temp.length - 1;
            }
        }
        item = temp;
    }
    @Override
    public void addFirst(T x) {
        if (item.length == size) {
            resize((int) (size * 1.5));
        }
        size += 1;
        item[nextFirst] = x;
        if (nextFirst == nextLast && size != item.length) {
            nextLast = findnext(nextLast);
        }
        nextFirst = findbefore(nextFirst);

    }
    @Override
    public void addLast(T x) {
        if (item.length == size) {
            resize((int) (size * 1.5));
        }
        size += 1;
        item[nextLast] = x;
        if (nextFirst == nextLast && size != item.length) {
            nextFirst = findbefore(nextFirst);
        }
        nextLast = findnext(nextLast);

    }
    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(item[i] + " ");
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        //T temp = findFirst();
        if (size < item.length / 4 && item.length >= 16) {
            resize(item.length / 4);
        }
        nextFirst = findnext(nextFirst);
        return item[nextFirst];
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        if (size < item.length / 4 && item.length >= 16) {
            resize(item.length / 4);
        }
        nextLast = findbefore(nextLast);
        return item[nextLast];
    }
    @Override
    public T get(int index) {
        if (nextFirst + 1 + index >= item.length) {
            index = (nextFirst + 1 + index) - item.length;
        } else {
            index = nextFirst + 1 + index;
        }
        return item[index];
    }
    public int size() {
        return size;
    }
    public Iterator<T> iterator() {
        return new ArrayIterator();
    }
    private class ArrayIterator implements Iterator {
        private int pos;
        private int count;
        private ArrayIterator() {
            pos = findnext(nextFirst);
            count = 0;
        }

        @Override
        public boolean hasNext() {
            return size > count;
        }

        @Override
        public T next() {
            T i = item[pos];
            pos = findnext(pos);
            count += 1;
            return i;
        }
    }
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque p = (Deque) o;
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
        return true;
    }
}
