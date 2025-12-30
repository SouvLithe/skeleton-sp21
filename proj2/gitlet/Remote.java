package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Merge.findAllAncestors;
import static gitlet.Repository.OBJECTS_DIR;
import static gitlet.Utils.*;

/**
 * 表示 gitlet 的远程对象。
 *
 * @author SouvLithe
 */
public class Remote implements Serializable {
    /**
     * KEY is the name of the remote
     * VALUE is the path of the remote
     */
    private final Map<String, File> remotes;

    Remote() {
        remotes = new HashMap<>();
    }

    /**
     * 将给定分支中的所有新对象移动到目标分支
     * 从包含该分支的存储库移动到目标存储库
     */
    private static void moveObjects(File sourceObjectDir, File targetObjectDir,
                                    Branch branch, Set<String> ancestors) {
        String branchHEAD = branch.getHEADAsString();
        for (String commit : ancestors) {
            moveObject(sourceObjectDir, targetObjectDir, commit);
            HelperMethods.toCommit(commit, targetObjectDir)
                    .getBlobs().values()
                    .forEach(objID -> moveObject(sourceObjectDir, targetObjectDir, objID));
            if (commit.equals(branchHEAD)) {
                break;
            }
        }
    }

    /**
     * 将对象（一个提交记录或一个数据块）从源仓库移动到目标仓库*
     *
     * @param id 要移动的对象的标识符
     */
    private static void moveObject(File sourceObjectDir, File targetObjectDir, String id) {
        String dir = id.substring(0, 2);
        String name = id.substring(2);
        File targetDir = join(targetObjectDir, dir);
        targetDir.mkdir();
        File sourcePath = join(sourceObjectDir, dir, name);
        writeContents(join(targetDir, name), readContents(sourcePath));
    }

    /**
     * 将远程 Gitlet 仓库中的提交信息导入到本地 Gitlet 仓库中。
     * 实际上，这会将远程仓库中给定分支的所有提交和数据块（那些尚未存在于当前仓库中的）复制到本地 .gitlet 文件夹中的名为
     * [远程名称]/[远程分支名称] 的分支中（就像在真正的 Git 中那样），并将 [远程名称]/[远程分支名称] 更改为指向当前提交的头指针（从而将远程仓库中的分支内容复制到当前仓库中）。
     * 如果该分支之前不存在于本地仓库中，则会在本地仓库中创建该分支。
     * */
    public void fetch(String remoteName,Branch branch) {
        File sourceRepo = remotes.get(remoteName);
        if (branch == null) {
            HelperMethods.exit("That remote does not have that branch.");
        }
        // 拿到所有在远程活跃分支中的 commit
        File sourceObjectsDir = join(sourceRepo, "objects");
        Commit sourceHEAD = HelperMethods.toCommit(
                readObject(join(sourceRepo, "HEAD"), Branch.class).getHEADAsString(),
                sourceObjectsDir);
        Set<String> ancestors = new LinkedHashSet<>();
        findAllAncestors(sourceHEAD, ancestors);

        //把这些 commit 移动到当前repo，fetch 它
        moveObjects(sourceObjectsDir,OBJECTS_DIR,branch,ancestors);
        String branchName = remoteName + "/"+branch;
        Branch nb;
        if (!Branch.isExists(branchName)) {
            nb = new Branch(branchName,branch.getHEADAsString());
        }else  {
            nb = Branch.readBranch(branchName);
            nb.setHEADContent(branch.getHEADAsString());
        }
        writeContents(join(Repository.BRANCHES_DIR,nb.toString()),nb);
    }

    /**
     * 尝试将当前分支的提交内容附加到
     * 给定远程分支的末尾处
     * */
    public void push(String remoteName,Branch branch) {
        // 拿到所有的 commit 在当前活跃的分支中
        File target = remotes.get(remoteName);
        Set<String> ancestors = new LinkedHashSet<>();
        Commit currentHEAD = HelperMethods.readHEADAsCommit();
        findAllAncestors(currentHEAD, ancestors);
        String branchHEAD = branch.getHEADAsString();
        if (!ancestors.contains(branchHEAD)) {
            HelperMethods.exit("Please pull down remote changes before pushing.");
        }

        // 移动这些 commit push 到远程的仓库，
        moveObjects(OBJECTS_DIR,join(target,"objects"),branch,ancestors);
        HelperMethods.setHEAD(currentHEAD,HelperMethods.readHEADAsBranch(),target);
    }

    /**
     * 将给定的登录信息保存到指定的远程名称下。
     * 接着，如果尝试推送或拉取来自指定的远程名称的操作，将会
     * 尝试使用这个.gitlet目录。
     */
    public boolean addRemote(String name, File path) {
        if (isExists(name)) {
            return false;
        }
        remotes.put(name, path);
        save();
        return true;
    }

    /**
     * 删除与指定远程名称相关的信息
     * 这里的意思是，如果想要更改您添加的某个远程服务器，
     * 那么您必须先将其删除，然后再重新添加
     */
    public boolean removeRemote(String name) {
        if (!isExists(name)) {
            return false;
        }
        remotes.remove(name);
        save();
        return true;
    }

    /**
     * 检验相同名称的 remote是否存在
     */
    public boolean isExists(String remote) {
        return remotes.containsKey(remote);
    }

    public File getRemote(String name) {
        return remotes.get(name);
    }

    /**
     * 序列化 remote 对象
     */
    public void save() {
        Utils.writeObject(Repository.REMOTES, this);
    }
}
