package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

/**
 *  代表 gitlet-checkout 和 gitlet-reset.
 *
 * @author SouvLithe
 */
public class CheckOut {
    /**
     * 将文件在主提交中的现有版本复制下来
     * 并将其放入工作目录中，
     * 如果已有该文件的旧版本，则会覆盖该旧版本。
     * 文件的新版本不会被暂存。
     * */
    public static void checkoutFile(File file) {
        checkoutFile(HelperMethods.readHEADAsCommit(),file);
    }

    /**
     * 选取具有给定 ID 的提交中该文件的现有版本，
     * 并将其放入工作目录中，
     * 如果该文件已在工作目录中，则会覆盖其原有版本。
     * 文件的新版本不会被暂存。
     * */
    public static void checkoutFile(Commit commit,File file) {
        String oldBlob = commit.getBlob(file);
        if (oldBlob == null) {
            HelperMethods.exit("File does not exist in that commit.");
        }
        // 重写老文件
        File checkFrom = join(Repository.makeObjectDir(oldBlob));
        reStoreBlob(file, checkFrom);
    }

    public static void checkoutBranch(String name) {
        if (!Branch.isExists(name)) {
            HelperMethods.exit("No such branch exists.");
        }

        Branch currentBranch = HelperMethods.readHEADAsBranch();
        if (currentBranch.toString().equals(name)) {
            HelperMethods.exit("No need to checkout the current branch.");
        }

        HelperMethods.untrackedExist();
        Repository.clean(Repository.CWD);

        Branch branchToSwitch = Branch.readBranch(name);
        Commit commitToSwitch = branchToSwitch.getHEADAsCommit();
        HashMap<String, String> old = commitToSwitch.getBlobs();
        for (String oldFile : old.keySet()) {
            String branchName = old.get(oldFile);
            reStoreBlob(join(oldFile),join(Repository.makeObjectDir(branchName)));
        }

        HelperMethods.readStagingArea().cleanStagingArea();
        HelperMethods.setHEAD(commitToSwitch,branchToSwitch);
    }

    /**
     * 检查给定提交记录的所有文件。
     * 删除不在该提交中的已跟踪文件。
     * 同时将当前分支的分支头移动到该提交节点处。
     * 请参阅介绍部分以了解使用“重置”后分支指针会发生什么情况的示例。
     * [提交 ID] 可以像进行“检出”操作时那样进行缩写。
     * 暂存区被清空。
     * 此命令本质上是对任意提交的检出操作，
     * 同时还会更改当前分支的分支头。
     * */
    public static void reset(Commit commit) {
        Repository.clean(Repository.CWD);
        HelperMethods.readStagingArea().cleanStagingArea();
        Map<String, String> olds = commit.getBlobs();
        olds.keySet().forEach(file -> checkoutFile(commit,join(file)));
        HelperMethods.setHEAD(commit,HelperMethods.readHEADAsBranch());
    }

    /**
     * 从blob中读取文件内容, 随后写入该文件
     *
     * @param file      需要检出的文件
     * @param checkFrom 指向 该文件的 blob
     */
    private static void reStoreBlob(File file,File checkFrom) {
        Blob oldBlob = readObject(checkFrom, Blob.class);
        writeContents(file, oldBlob.getContent());
    }
}
