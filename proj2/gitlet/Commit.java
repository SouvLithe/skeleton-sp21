package gitlet;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 * 这个 commit 结构包含有: message , id , timestamp
 * parent pointer , files pointer
 *
 * @author SouvLithe
 */
public class Commit implements Serializable {
    /**
     * The message of this Commit.
     */
    private String log;

    /**
     * Commit 的 parent commit
     */
    private String parent;

    /**
     * 在创建 merge commit 时，第二个 parent 版本不会为空。
     */
    private String parent2;

    /**
     * Commit 的 timestamp 时间戳
     */
    private Date date;

    /**
     * 此次提交的文件快照。
     * 键是当前工作目录中具有绝对路径的文件。
     * 值是位于 BLOB_DIR/shortCommitUid 目录下的二进制数据块。
     */
    private HashMap<String, String> blobs;

    /**
     * 维护该 Commit 的一个 SHA-1 id
     */
    private String uid;

    /**
     * 创建一个带有消息和父提交标识符的提交对象。
     * 第一个带有消息且没有父提交“initial commit” 的 commit。
     * 也就是 sentinel 节点，避免过度遍历
     */
    public Commit(String msg, String parent) {
        instantiateCommit(msg, parent, null);
    }

    public Commit(String msg, String parent, String parent2) {
        instantiateCommit(msg, parent, parent2);
    }

    /**
     * 找到与 uid 相匹配的 commit对象
     */
    public static Commit findWithUid(String uid) {
        if (uid == null) {
            return null;
        }
        Commit ret = HelperMethods.toCommit(uid);
        return ret != null ? ret : HelperMethods.toCommit(uid.substring(0, 8));
    }

    /**
     * 找出所有具有指定提交消息的提交记录的 id 值
     *
     * @return commits 的uid刘表
     */
    public static List<String> findWithMessage(String message) {
        Set<Commit> commits = findAll();
        List<String> ids = new ArrayList<>();
        for (Commit c : commits) {
            if (c.log.equals(message)) {
                ids.add(c.uid);
            }
        }
        return ids;
    }

    /**
     * @return 所有曾经进行过的 commit 操作，包括未关联的。
     */
    public static Set<Commit> findAll() {
        Set<Commit> commits = new HashSet<>();
        String cs = readContentsAsString(Repository.COMMITS);
        while (cs != null && !cs.isEmpty()) {
            commits.add(HelperMethods.toCommit(cs.substring(0, 40)));
            cs = cs.substring(40);
        }
        return commits;
    }

    /**
     * 标准化 Commit 对象的创建
     */
    private void instantiateCommit(String msg, String first, String second) {
        this.log = msg;
        this.parent = first;
        this.parent2 = second;
        if (first == null) {
            // new Date(0) 这个值表示从 1970年1月1日00:00:00 UTC 开始经过的 毫秒数
            this.date = new Date(0);
        } else {
            this.date = new Date();
        }
        this.blobs = new HashMap<>();
    }

    /**
     * 将此 commit 对象写入 COMMIT_DIR 目录
     * 并重置 HEAD 指针
     */
    public void makeCommit() {
        //加载父母类的 blobs
        if (this.parent != null) {
            this.blobs = this.getParentAsCommit().blobs;
        }
        Index index = HelperMethods.readStagingArea();
        boolean flag = getStage(index);
        flag = unStage(flag, index);
        if (this.parent != null && !flag) {
            HelperMethods.exit("No changes added to the commit.");
        }
        setUid();
        File out = Repository.makeObjectDir(this.uid);
        index.cleanStagingArea();
        writeContents(out, this);
        HelperMethods.setHEAD(this, HelperMethods.readHEADAsBranch());
        String cs = readContentsAsString(Repository.COMMITS);
        cs += this.uid;
        writeContents(Repository.COMMITS, cs);
    }

    /**
     * 为blobs 添加 staging区域
     */
    private boolean getStage(Index i) {
        boolean flag = false;
        Set<String> rm = i.getRemoved();
        if (!rm.isEmpty()) {
            flag = true;
            rm.forEach(file -> {
                this.blobs.remove(file);
                restrictedDelete(file);
            });
        }
        return flag;
    }

    /**
     * 在 commit 中删掉 blobs
     */
    private boolean unStage(boolean flag, Index i) {
        Set<String> rm = i.getRemoved();
        if (!rm.isEmpty()) {
            flag = true;
            rm.forEach(file -> {
                blobs.remove(file);
                restrictedDelete(file);
            });
        }
        return flag;
    }

    /**
     * getters 和 setters
     */
    public String getUid() {
        return this.uid;
    }

    public void setUid() {
        this.uid = sha1(this.parent + this.date + this.log);
    }

    public Date getDate() {
        return date;
    }

    public String getParentAsString() {
        return parent;
    }

    public Commit getParentAsCommit() {
        return HelperMethods.toCommit(this.parent);
    }

    public String getLog() {
        return log;
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    public String getBlob(File f) {
        return blobs.get(f.getAbsolutePath());
    }

    public Commit getSecondParentAsCommit() {
        return HelperMethods.toCommit(this.parent2);
    }

}
