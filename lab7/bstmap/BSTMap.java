package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private int size;
    private BSTNode root;

    private class BSTNode{
        K key;
        V value;
        BSTNode lc, rc;
        private BSTNode(K k, V v) {
            key = k;
            value = v;
            lc = null;
            rc = null;
        }
    }
    public BSTMap() {
        size = 0;
        root = null;
    }
    public void clear() {
        root = null;
        size = 0;
    }
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }
    private boolean containsKey(BSTNode node, K key) {
        if (node == null) return false;
        int cmp = node.key.compareTo(key);
        if (cmp == 0) {
            return true;
        }
        else if (cmp < 0) {
            return containsKey(node.lc, key);
        }
        else if (cmp > 0) {
            return containsKey(node.rc, key);
        }
        return true;
    }
    public V get(K key) {
        return get(root, key);
    }
    private V get(BSTNode node, K key) {
        if(node == null) return null;
        int cmp = node.key.compareTo(key);
        if(cmp == 0) return node.value;
        if(cmp < 0) return get(node.lc, key);
        if(cmp > 0) return get(node.rc, key);
        return null;
    }
    public int size() {
        return size;
    }
    public void put(K key, V value) {
        root = put(root, key, value);
        size += 1;
    }
    private BSTNode put(BSTNode node, K key, V value) {
        if(node == null) return new BSTNode(key,value);
        int cmp = node.key.compareTo(key);
        if(cmp < 0) {
            node.lc = put(node.lc, key ,value);
        }
        if(cmp > 0) {
            node.rc = put(node.rc, key, value);
        }
        return node;
    }
    public void printInOrder() {

    }
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
