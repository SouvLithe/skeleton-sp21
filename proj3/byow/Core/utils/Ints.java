package byow.Core.utils;

/**
 * 整数操作工具类，提供类似Guava Ints的功能
 */
public class Ints {

    /**
     * 返回多个整数中的最小值
     * @param values 可变参数，至少需要一个参数
     * @return 最小值
     * @throws IllegalArgumentException 如果values为空
     */
    public static int min(int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("Ints.min() requires at least one argument");
        }
        int min = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * 返回多个整数中的最大值
     * @param values 可变参数，至少需要一个参数
     * @return 最大值
     * @throws IllegalArgumentException 如果values为空
     */
    public static int max(int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("Ints.max() requires at least one argument");
        }
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * 检查数组中是否包含指定值
     * @param array 要搜索的数组
     * @param target 要查找的值
     * @return 如果找到返回true，否则返回false
     */
    public static boolean contains(int[] array, int target) {
        for (int value : array) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将整数列表转换为int数组
     * @param list 整数列表
     * @return int数组
     */
    public static int[] toArray(java.util.List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}