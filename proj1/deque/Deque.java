package deque;

public interface Deque<T> {

    /** 将类型为T的项添加到队列的前面。可假设该项目永远不会为空 */
    void addFirst(T item);

    /** 将类型为T的项添加到队列的后面。可假设该项目永远不会为空 */
    void addLast(T item);

    /** 如果deque为空则返回true，否则返回false */
    default boolean isEmpty() {
        return size() == 0;
    }

    /** 返回deque的项数 */
    int size();

    /** 以空格分隔，从首到尾打印队列中的项。打印完所有项目后，再打印一行 */
    void printDeque();

    /** 移除并返回队列前面的项。如果不存在这样的项，则返回null */
    T removeFirst();

    /** 移除并返回队列后面的项。如果不存在这样的项，则返回null */
    T removeLast();

    /** 获取给定索引处的项，其中0是前一项，1是下一项，依此类推。如果不存在这样的项，则返回null。千万不要改变队列！ */
    T get(int index);
}
