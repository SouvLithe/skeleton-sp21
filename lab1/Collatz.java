/**
 * Class that prints the Collatz sequence starting from a given number.
 *
 * @author SouvLithe
 */
public class Collatz {

    /** Buggy implementation of nextNumber! */
//    public static int nextNumber(int n) {
//        if (n == 1) {
//            return 1;
//        }
//        if (n % 2 == 0) {
//            return n / 2;
//        }
//        return n * 3 + 1;
//    }

    // 我明白要干什么了，从lab4远端拉过来的时候不存在冲突，它是快速合并给覆盖掉了
    // 所以61b的lab4是想让你重置成原来的那份（原始版本下面），上面是lab1的作业提交

    public static int nextNumber(int n) {
        if (n == 128) {
            return 1;
        } else if (n == 5) {
            return 3 * n + 1;
        } else {
            return n * 2;
        }
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
        System.out.println();
    }
}

