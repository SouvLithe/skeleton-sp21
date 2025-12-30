package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 * Represents helper methods,辅助方法集锦
 *
 * @author SouvLithe
 */

public class HelperMethods {

    /**
     * 如果 .gitlet 存在的话返回true
     */
    public static void exitUnlessRepoExists() {
        File repo = join(CWD, ".gitlet");
        if (!repo.exists()) {
            exit("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * 判断 operands 的数量是否正确,
     * exit(0) 如果 operands 不正确.
     */
    public static void judgeOperands(int min, int max, String[] args) {
        if (args.length < min + 1 || args.length > max + 1) {
            exit("Incorrect operands.");
        }
    }

    public static void judgeOperands(String[] args, int num) {
        judgeOperands(num, num, args);
    }

    public static void judgeCommand(String[] args, int num) {
        exitUnlessRepoExists();
        judgeOperands(args, num);
    }

    /**
     * 使用给定的 ID（长度为 8 或 40）进行提交操作*
     *
     * @param uid：提交的唯一标识符
     * @param targetDir：该提交所在的目录
     * @return 如果存在具有给定 uid 的提交，则返回该提交内容
     */
    public static Commit toCommit(String uid, File targetDir) {
        File commit = getObject(uid, targetDir);
        if (commit == null) {
            return null;
        }
        return commit.exists() ? readObject(commit, Commit.class) : null;
    }

    /**
     * 使用给定的 ID（长度为 8 或 40）进行提交操作*
     *
     * @param uid - 提交的唯一标识符
     * @return 如果存在指定的 uid，则返回该提交信息
     */
    public static Commit toCommit(String uid) {
        return toCommit(uid, OBJECTS_DIR);
    }

    /**
     * 使用给定的 id(40-len) 生成blob
     *
     * @param uid uid of the blob
     * @return the blob with given uid if exists
     */
    public static Blob toBlob(String uid) {
        File b = getObject(uid, OBJECTS_DIR);
        if (b == null) {
            return null;
        }
        return b.exists() ? readObject(b, Blob.class) : null;
    }

    /**
     * @return object filepath
     */
    private static File getObject(String uid, File objectDir) {
        if (uid == null || uid.isEmpty()) {
            return null;
        }
        File obj = join(objectDir, uid.substring(0, 2));
        String rest = getObjectName(uid);
        if (uid.length() == 8) {
            List<String> objects = plainFilenamesIn(obj);
            if (objects == null) {
                return null;
            }
            for (String commit : objects) {
                if (commit.substring(0, 6).equals(rest)) {
                    obj = join(obj, commit);
                    break;
                }
            }
        } else {
            obj = join(obj, rest);
        }
        return obj;
    }

    /**
     * 在打印信息前退出
     */
    public static void exit(String message) {
        if (message != null) {
            System.out.println(message);
        }
        System.exit(0);
    }

    /**
     * 检查是否有任何未跟踪的文件。
     */
    public static void untrackedExist() {
        if (!Status.getUntrackedFilesNames().isEmpty()) {
            exit("There is an untracked file in the way; delete it,"
                    + " or add and commit it first.");
        }
    }

    /**
     * @return 在不同的系统中返回正确的路径
     */
    public static File correctPath(String path) {
        path = path.replace("/", File.separator);
        return join(path);
    }

    /**
     * @return index对象
     */
    public static Index readStagingArea() {
        return readObject(INDEX, Index.class);
    }

    public static Remote readRemotes() {
        return readObject(REMOTES, Remote.class);
    }

    /**
     * @return 当前branch指针.
     */
    public static Branch readHEADAsBranch() {
        return readObject(HEAD, Branch.class);
    }

    /**
     * @return HEAD指针指向的 commit id
     */
    public static String readHEADContent() {
        return readHEADAsBranch().getHEADAsString();
    }

    /**
     * @return HEAD指向的 commit
     */
    public static Commit readHEADAsCommit() {
        String uid = readHEADContent();
        return HelperMethods.toCommit(uid);
    }


    /**
     * getters 和 setters
     */
    public static void setHEAD(Commit commit, Branch b, File remote) {
        b.setHEADContent(commit.getUid());
        writeObject(join(remote, "HEAD"), b);
        b.updateBranch();
    }

    public static void setHEAD(Commit commit, Branch b) {
        b.setHEADContent(commit.getUid());
    }


}
