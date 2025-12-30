package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import static gitlet.Utils.*;

/**
 * 展示 gitlet 的目录结构
 * <p>
 * .gitlet
 * <br>├── refs/
 * <br>│ ├── commits
 * <br>│ ├── heads/
 * <br>│ └── remotes/
 * <br>├── objects/
 * <br>├── HEAD
 * <br>└── index
 *
 * @author SouvLithe
 */
public class Repository implements Serializable {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * 引用目录
     */
    public static final File REPO_DIR = join(GITLET_DIR, "refs");

    /**
     * 分支目录
     */
    public static final File BRANCHES_DIR = join(REPO_DIR, "heads");

    /**
     * The commits file contains all commits' id.
     */
    public static final File COMMITS = join(REPO_DIR, "commits");
    public static final File REMOTES = join(REPO_DIR, "remotes");

    /**
     * 存blobs 和 commits的目录
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

    /**
     * 头指针
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");

    /**
     * 存储了已添加文件和已删除文件引用的索引对象
     */
    public static final File INDEX = join(GITLET_DIR, "index");

    /**
     * 在当前目录中创建一个新的 Gitlet 版本控制系统。
     * */
    public static void initializeRepo() {
        List<File> dirs = List.of(GITLET_DIR, REPO_DIR, BRANCHES_DIR, COMMITS, REMOTES, HEAD, INDEX);
        dirs.forEach(File::mkdir);
        Branch head = new Branch("master", "");
        writeObject(HEAD, head);
        head.updateBranch();
        writeObject(INDEX,new Index());
        writeObject(REMOTES,new Remote());
        writeContents(COMMITS,"");
    }

    /**
     * 删除 DIR 中的所有文件
     */
    public static void clean(File dir) {
        List<String> files = plainFilenamesIn(dir);
        if (files != null && !files.isEmpty() ) {
            files.forEach(file -> join(dir,file).delete());
        }
    }

    /**
     * 获取一个 commit 对象或一个 blob 对象的目录信息，
     * 同时提供其前两个标识符。
     */
    public static File getObjectsDir(String id){
        return join(OBJECTS_DIR, id.substring(0,2));
    }

    /**
     * 获取 commit 对象或 blob 对象的文件名，
     * 同时附带其最后 38 位的标识符。
     */
    public static String getObjectName(String id){
        return id.substring(2);
    }

    /**
     * 获取某个 commit 对象或某个 blob 对象的文件路径
     * （该对象具有其对应的 ID）。
     */
    public static File makeObjectDir(String id){
        File out = getObjectsDir(id);
        out.mkdir();
        return join(out,getObjectName(id));
    }

    /**
     * 获取给定远程仓库分支目录的文件路径。
     */
    public static File getRemoteBranchDir(String name){
        return join(HelperMethods.readRemotes().getRemote(name))
    }
}

