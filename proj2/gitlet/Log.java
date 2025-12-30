package gitlet;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Represents gitlet-log, gitlet-global-log.
 *
 * @author SouvLithe
 */
public class Log {
    /**
     * 从当前的主提交点开始，
     * 展示每个提交的相关信息
     * 依次沿着提交树向后追溯，直至最初的提交点，
     * 遵循第一个父提交的链接，忽略在合并提交中发现的任何第二个父提交。
     * */
    public static void log(Commit c) {
        while (c != null) {
            printLog(c);
            c = Commit.findWithUid(c.getParentAsString());
        }
    }

    /**
     * Displays information about all commits ever made.
     */
    public static void globalLog() {
        Commit.findAll().forEach(Log::printLog);
    }

    /**
     * Display log in this format
     * <br><br>
     * ===
     * <br>
     * commit {commit id}
     * <br>
     * Date: E MMM dd HH:mm:ss yyyy Z {commit timestamp}
     * <br>
     * {commit message}
     */
    private static void printLog(Commit c) {
        System.out.println("===");
        System.out.println("commit " + c.getUid());
        SimpleDateFormat d = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        System.out.println("Date: " + d.format(c.getDate()));
        System.out.println(c.getLog() + "\n");
    }
}
