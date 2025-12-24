package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    /**
     * 定义单个节点的数据结构
     */
    private class Node {
        // 自引用结构
        private Node prev = null;
        private Node next = null;
        // 实际存储内容
        private T content;

        // 给内容赋值
        Node(T content) {
            this.content = content;
        }

        // 辅助下面的 getRecursion
        public T recursive(int index) {
            if (index == 0) {
                return content;
            } else {
                assert next != null;
                return next.recursive(index - 1);
            }
        }
    }

    // 维护一个 size，让查长度的时间可以缩减至常数时间
    private int size;
    //  维护一个 sentinel，特值处理，使得每个节点都有一个前驱一个后继
    private Node sentinel;

    /**
     * 利用链表节点组装链表
     */
    public LinkedListDeque() {
        sentinel = new Node(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node n = new Node(item);
        // 2. 将新节点 n 的 next 指向当前 sentinel 的 next
        //    （当前 sentinel.next 是原来的第一个节点）
        n.next = sentinel.next;

        // 3. 将原来第一个节点的 prev 指向新节点 n
        //    （现在 sentinel.next 还是原来的第一个节点）
        sentinel.next.prev = n;

        // 4. 将 sentinel 的 next 指向新节点 n
        //    （现在新节点 n 成为第一个节点）
        sentinel.next = n;

        // 5. 将新节点 n 的 prev 指向 sentinel
        n.prev = sentinel;

        // 6. 链表大小加 1
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node n = new Node(item);
        sentinel.prev.next = n;
        n.prev = sentinel.prev;
        n.next = sentinel; // 这步决定了是尾插
        sentinel.prev = n;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node current = sentinel.next;
        // 当前节点不为尾节点时,打印值并向下查找
        while (current != sentinel) {
            System.out.println(current.content);
            System.out.println(" ");
            current = current.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        Node temp = sentinel.next;
        sentinel.next = temp.next;
        temp.next.prev = sentinel;
        size -= 1;
        return temp.content;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        Node temp = sentinel.prev;
        sentinel.prev = temp.prev;
        temp.prev.next = temp.next;
        size -= 1;
        return temp.content;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        Node current = sentinel.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.content;
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        // 初始化位置指针
        private int position = 0;

        @Override
        public boolean hasNext() {
            return position < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                return null;
            }
            T rlt = get(position);
            position += 1;
            return rlt;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    //渐进式设计
    @Override
    public boolean equals(Object o) {
        // 查看引用地址是否相同
        if (this == o) {
            return true;
        }
        // 看看两者类型是否相等
        if (o instanceof Deque) {
            Deque<T> target = (Deque<T>) o;
            //两者大小是否相等
            if (size != target.size()) {
                return false;
            }
            //两者中的每个元素内容是否相等
            for (int i = 0; i < size; i++) {
                if (!target.get(i).equals(this.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Same as get, but uses recursion.
     */
    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return sentinel.next.recursive(index);
    }
}
