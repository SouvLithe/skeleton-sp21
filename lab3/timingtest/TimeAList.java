package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        //对 Alist 进行测试
        // 1-创造 printTimingTable 对应的三个AList，进行参数初始化
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        //维护 n 的初始值：测试多少的量
        int n = 1000;
        //列表中只有 8 行所以遍历 8 次
        for (int i = 0; i < 8; i++) {
            // 先把n加进去
            Ns.addLast(n);
            // 2-开始记录调用的 初始时间
            Stopwatch sw = new Stopwatch();
            // 3-创建临时数组，进行qps测试
            AList<Integer> temp = new AList<>();
            //初始化值
            int ops = 0;
            for (int j = 0; j < n; j++) {
                //查看是否会漏值-与Ns的值比较一下就可以知道了
                temp.addLast(1);
                ops++;
            }
            // 4-拿到运行时长
            double ms = sw.elapsedTime();
            times.addLast(ms);
            opCounts.addLast(ops);
            n = n * 2;
        }
        printTimingTable(Ns, times, opCounts);
    }
}
