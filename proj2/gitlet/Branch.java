package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import static gitlet.Repository.BRANCHES_DIR;
import static gitlet.Utils.join;

/**
 * gitlet 的 branch pointer 对象.
 *
 * @author SouvLithe
 */
public class Branch implements Serializable {
    /**
     * 分支的名字,如 master 这种
     */
    private final String name;

    /**
     * HEAD commit uid
     */
    private String HEAD;

    /**
     * 根据分支名称和 HEAD 提交的唯一标识符创建一个分支对象。
     * 若存在同名的分支，则退出操作。
     */
    public Branch(String name, String head) {
        if (isExists(name)) {
            HelperMethods.exit("A branch with that name already exists.");
        }
        this.name = name;
        this.HEAD = head;
    }

    /**
     * 测试给定名称的分支是否存在
     */
    public static boolean isExists(String name) {
        name = correctName(name);
        // plainFilenamesIn 拿到 目录 DIR 中所有普通文件的名称列表（以 Java 字符串形式，按字典顺序排列）。
        // 若 DIR 不表示一个目录，则返回 null
        List<String> names = Utils.plainFilenamesIn(BRANCHES_DIR);
        return names != null && names.contains(name);
    }

    /**
     * 把当前 HEAD 写入分支
     */
    public void updateBranch() {
        // readObject    从文件中读取类型为 T 的对象，并将其强制转换为 EXPECTEDCLASS 类型。
        // 若出现问题，则抛出 IllegalArgumentException 异常。
        this.HEAD = Utils.readObject(Repository.HEAD, Branch.class).getHEADAsString();
        String n = this.name;
        n = correctName(n);
        File h = join(BRANCHES_DIR, n);
        Utils.writeObject(h, this);
    }

    public static String correctName(String name) {
        // 将目录中的斜杠全部替换掉
        return name.replace("/", "_");
    }

    /**
     * Read branch object with given name
     */
    public static Branch readBranch(String branchName) {
        return readBranch(branchName, BRANCHES_DIR);
    }
    // 被楼上函数调用
    public static Branch readBranch(String branchName, File branchDir) {
        branchName = correctName(branchName);
        File b = join(branchDir, branchName);
        return !b.exists() ? null : Utils.readObject(b, Branch.class);
    }

    /**
     * 删掉给定名字的分支
     *
     * @return true if and only if branch exists and is successfully deleted,
     * false otherwise.
     */
    public boolean remove(String branchName) {
        File b = join(BRANCHES_DIR, branchName);
        return b.delete();
    }

    public void setHEADContent(String content) {
        this.HEAD = content;
    }

    public String getHEADAsString() {
        return this.HEAD;
    }

    public Commit getHEADAsCommit() {
        return HelperMethods.toCommit(this.HEAD);
    }

    public String getName() {
        return name;
    }

    /**
     * @return branch name
     */
    @Override
    public String toString() {
        return correctName(name);
    }
}
