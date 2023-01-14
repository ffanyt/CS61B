package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {


    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;
        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private double loadFactor;
    private int size;
    private static final int SIZE = 16;
    private static final double FACTOR = 0.75;

    /** Constructors */
    public MyHashMap() {
        this(SIZE, FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {

        Node n = new Node(key, value);
        return n;
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(SIZE);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (buckets == null) {
            return false;
        }
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        Node n = getNode(key);
        if (n != null) {
            return n.value;
        }
        return null;
    }
    private Node getNode(K key) {
        int index = getIndex(key, buckets.length);
        for (Node n : buckets[index]) {
            Node temp = n;
            K k = n.key;
            V v = n.value;
            if (key.equals(n.key)) {
                return n;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int index = getIndex(key, buckets.length);
        Node no = getNode(key);
        if (no != null) {
            no.value = value;
            return;
        }

        Node n = new Node(key, value);
        buckets[index].add(n);
        size += 1;
        if(hasResize()) {
            resize(buckets.length * 2);
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (int i = 0; i < buckets.length; i++) {
            for (Node n : buckets[i]) {
                set.add(n.key);
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapNodeIterator();
    }
    private int getIndex(K key, int len) {
        int hase = key.hashCode();
        return Math.floorMod(hase, len);
    }
    private boolean hasResize() {
        return (double) (size / buckets.length) >= loadFactor;
    }
    private void resize(int newsize) {
        Collection[] newtable = createTable(newsize);
        for (int i = 0; i < buckets.length; i++) {
            for (Node n : buckets[i]) {
                int index = getIndex(n.key, newsize);
                newtable[index].add(n);
            }
        }
        buckets = newtable;
    }
    private class MyHashMapNodeIterator implements Iterator{
        Queue<Node> queue;
        public void MyHashMapNodeIteratorIterator() {
            queue = new LinkedList<>();
            for(Collection<Node> n : buckets) {
                for(Node node : n) {
                    queue.add(node);
                }
            }
        }
        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public Object next() {
            return queue.poll();
        }
    }
}
