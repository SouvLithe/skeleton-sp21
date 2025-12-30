package gitlet;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gitlet.HelperMethods.readHEADAsCommit;
import static gitlet.HelperMethods.readStagingArea;
import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;

/**
 * 表示 gitlet-status.
 *
 * @author Edward Tsang
 */
public class Status {

    /**
     * 显示当前存在的分支
     * 并用星号标记当前分支
     * 还会显示已暂存以进行添加或删除的文件
     * 它应遵循的准确格式示例如下。
     */
    public static void printStatus() {
        Index idx = HelperMethods.readStagingArea();
        printFilenames("=== Branches ===", getBranchesNames());
        printFilenames("\n=== Staged Files ===", idx.getAddedFilenames());
        printFilenames("\n=== Removed Files ===", idx.getRemovedFilenames());
        printFilenames("\n=== Modifications Not Staged For Commit ===",
                getModifiedButNotStagedFilesNames());
        printFilenames("\n=== Untracked Files ===", getUntrackedFilesNames());
        System.out.println();
    }

    /**
     * 打印信息和文件名称
     */
    private static void printFilenames(String msg, List<String> names) {
        System.out.println(msg);
        if (names != null) {
            names.forEach(System.out::println);
        }
    }

    private static void printFilenames(String msg, Set<String> names) {
        printFilenames(msg, new ArrayList<>(names));
    }

    /**
     * Get all branches' names.
     * The current HEAD branch has a `*` in front of its name
     * e.g. *master.
     */
    private static List<String> getBranchesNames() {
        List<String> bs = plainFilenamesIn(Repository.BRANCHES_DIR);
        if (bs == null) {
            return null;
        }
        ArrayList<String> branches = new ArrayList<>(bs);
        String name = HelperMethods.readHEADAsBranch().toString();
        branches.remove(name);
        branches.add(0, "*" + name);
        return branches;
    }

    /**
     * 如果一个文件处于工作目录中，并且
     * <p>
     * 在当前提交中被跟踪、在工作目录中进行了修改，但尚未进行暂存操作，那么该文件就被视为“已修改但未暂存”。或者
     * <br>已准备就绪以进行添加操作，但其内容与工作目录中的内容不同；或者
     * <br>已准备进行添加操作，但在工作目录中已被删除；或者
     * <br>并未进行移除操作，
     * 而是在当前提交中进行了记录，并从工作目录中删除了。
     */
    private static Set<String> getModifiedButNotStagedFilesNames() {
        Index judge = readStagingArea();
        HashSet<String> ret = new HashSet<>();
        Commit h = readHEADAsCommit();
        for (String filePath : h.getBlobs().keySet()) {
            File file = join(filePath);
            String fileName = file.getName();
            boolean exists = file.exists();
            boolean staged = judge.isStaged(file);
            boolean removed = judge.isRemoved(file);
            boolean tracked = judge.isTracked(file, h);
            boolean modified = Index.isModified(file, h);
            if (!exists && (staged || (!removed && tracked))) {
                ret.add(fileName + " (deleted)");
            } else if (exists && modified && (tracked || staged)) {
                ret.add(fileName + " (modified)");
            }
        }
        return ret;
    }

    /**
     * 工作目录中存在但既未准备添加也未被跟踪的文件。
     * 这包括那些曾被准备移除但之后在 Gitlet 不知情的情况下又被重新创建的文件。*
     *
     * @return 未跟踪的文件名，如果模式不正确则返回空集。
     */
    public static Set<String> getUntrackedFilesNames() {
        HashSet<String> ret = new HashSet<>();
        Commit currentCommit = HelperMethods.readHEADAsCommit();
        List<String> files = plainFilenamesIn(Repository.CWD);
        if (files != null) {
            return ret;
        }
        Index index = readStagingArea();
        for (String file : files) {
            File f = join(Repository.CWD, file);
            boolean flag = index.isTracked(f, currentCommit);
            if (!flag) {
                ret.add(f.getName());
            }
        }
        return ret;
    }
}
