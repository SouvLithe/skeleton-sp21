package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /*
    *  Instance Variables
    * 装载因子、桶的初始数目大小、单个桶深的长度，桶内对象
     * */
    private final double loadFactor;
    private int size;
    private int length;
    // 请注意，在Java中，您不能创建参数化类型的数组。
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /**
     * Constructors
     */
    public MyHashMap() {
        size = 16;
        loadFactor = 0.75;
        length = 0;
        buckets = createTable(size);
    }

    public MyHashMap(int initialSize) {
        size = initialSize;
        loadFactor = 0.75;
        length = 0;
        buckets = createTable(size);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = initialSize;
        loadFactor = maxLoad;
        length = 0;
        buckets = createTable(size);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new HashSet<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    /**
     * Removes all of the mappings from this map.
     * TODO 下面的都是作业代码
     */
    @Override
    public void clear() {
        size = 16;
        length = 0;
        buckets = createTable(size);
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key
     */
    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     *
     * @param key
     */
    @Override
    public V get(K key) {
        int pos = getPosition(key);
        Collection<Node> set = buckets[pos];
        if (set == null) {
            return null;
        }
        for (Node p : set) {
            if (p.key.equals(key)) {
                return p.value;
            }
        }
        return null;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return length;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     *
     * @param key
     * @param value
     */
    @Override
    public void put(K key, V value) {
        if (isOverload()){
            resize();
        }
        Node p = createNode(key, value);
        int position = getPosition(key);
        if (buckets[position] == null) {
            buckets[position] = createBucket();
        }
        for (Node node : buckets[position]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        buckets[position].add(p);
        length++;
    }

    /**
     * Returns a Set view of the keys contained in this map.
     */
    @Override
    public Set<K> keySet() {
        if (length == 0) {
            return null;
        }
        HashSet<K> result = new HashSet<>();
        for (K k : this) {
            result.add(k);
        }
        return result;
    }

    /**
     * 如果存在指定键的映射，则从此映射中删除该映射。
     * 对于 lab8 来说，此操作并非必需。如果您未实现此功能，请抛出一个
     * UnsupportedOperationException 异常。
     *
     * @param key
     */
    @Override
    public V remove(K key) {
        int pos = getPosition(key);
        Collection<Node> set = buckets[pos];
        if (set == null) {
            return null;
        }
        for (Node p : set) {
            if (p.key.equals(key)) {
                set.remove(p);
                length--;
                return p.value;
            }
        }
        return null;
    }

    /**
     * 仅在指定键当前与指定值进行映射的情况下，才删除该键的条目。
     * 对于实验 8 来说，此操作并非必需。如果您未实现此功能，
     * 请抛出一个 “ UnsupportedOperationException ” 异常。
     *
     * @param key
     * @param value
     */
    @Override
    public V remove(K key, V value) {
        int pos = getPosition(key);
        Node p = createNode(key, value);
        Collection<Node> set = buckets[pos];
        if (set == null || !set.contains(p)) {
            return null;
        }
        buckets[pos].remove(p);
        length--;
        return value;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<K> iterator() {
        return new Iterator<>() {
            private final Collection<Node>[] b = buckets;
            private int pos = findPos(0);

            private Collection<Node> curBuck = b[pos];
            private Iterator<Node> curIter = curBuck.iterator();

            private int findPos(int cur) {
                int pos = cur;
                while (pos < size && b[pos] == null) {
                    pos++;
                }
                return pos;
            }

            @Override
            public boolean hasNext() {
                return curIter.hasNext() || findPos(pos + 1) < size;
            }

            @Override
            public K next() {
                if (curIter.hasNext()) {
                    Node curNode = curIter.next();
                    return curNode.key;
                }
                pos = findPos(pos + 1);
                curBuck = b[pos];
                curIter = curBuck.iterator();
                return curIter.next().key;
            }
        };
    }

    // Utils工具方法

    private void resize() {
        // 创建一个 MyHashMap 容量是原来的两倍
        MyHashMap<K, V> temp = new MyHashMap<>(size * 2);
        // 遍历原来的数组，将其放入 temp 这个临时的MyHashMap中
        for (K key : this) {
            temp.put(key, get(key));
        }
        // 更新size大小
        this.size *= 2;
        // 把变量赋给原本的变量
        this.buckets = temp.buckets;
    }

    private boolean isOverload() {
        return (double) length / size >= loadFactor;
    }

    private int getPosition(K key) {
        // Math.floorMod() 是 Java 中的一个数学函数，用于执行向下取整的模运算
        return Math.floorMod(key.hashCode(), size);
    }

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
}
