package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.join;
import static gitlet.Utils.restrictedDelete;

/**
 * 表示一个 gitlet index object, gitlet-add, gitlet-rm.
 * self-created
 *
 * @author SouvLithe
 */
public class Index implements Serializable {
    /**
     * The map of staged files.
     * File absolute path as KEY,
     * Blob name as VALUE.
     */
    private final Map<String, String> added;

    /**
     * 删除文件的集合 set
     */
    private final Set<String> removed;

    /**
     * 一组已被跟踪但尚未提交的文件。
     */
    private final Set<String> tracked;

    /**
     * 创建一个索引对象。
     * 索引是一个存储已归档文件、已删除文件和跟踪文件指针的对象
     */
    public Index() {
        added = new HashMap<>();
        removed = new HashSet<>();
        tracked = new HashSet<>();
    }

    /**
     * 测试文件在指定的提交操作中是否已被修改。
     *
     * @return 当且仅当文件存在，
     * 并且其状态与给定提交的状况不一致时，该条件为真。
     */
    public static boolean isModified(File inFile, Commit c) {
        if (!inFile.exists()) {
            return true;
        }
        String current = Blob.getBlobName(inFile);
        String oldBlobName = c.getBlob(inFile);
        return oldBlobName == null || !oldBlobName.equals(current);
    }

    public static boolean isModified(File inFile, Commit current, Commit target) {
        String cur = current.getBlob(inFile);
        String tar = target.getBlob(inFile);
        return !Objects.equals(tar, cur);
    }

    /**
     * 将当前文件的副本添加到暂存区域。
     * 因此，添加文件也被称为将文件暂存以进行添加。
     * 对已暂存的文件进行暂存会覆盖暂存区域中先前的条目（即新内容）。
     * 暂存区域应该在 .gitlet 中的某个位置。
     * 如果当前文件的工作版本与当前提交中的版本完全相同，
     * 则不要将其暂存以进行添加，并且如果它已经在暂存区域中，就将其从暂存区域中删除。
     * 如果在执行该命令时该文件处于暂存状态，则它将不再被暂存以删除。
     */
    public void add(File file) {
        String f = file.getAbsolutePath();
        if (isRemoved(file)) {
            removed.remove(f);
        }
        if (isModified(file, HelperMethods.readHEADAsCommit())) {
            added.put(f, new Blob(file).makeBlob());
            tracked.add(f);
        }
        save();
    }

    /**
     * 如果该文件当前已被标记为要添加，则取消其标记状态。
     * 如果该文件已在当前提交中被跟踪，
     * 将其标记为要删除，并从工作目录中移除该文件（如果用户尚未执行此操作）。
     */
    public boolean remove(File file) {
        boolean flag = false;
        String f = file.getAbsolutePath();
        if (isStaged(file)) {
            added.remove(f);
            flag = true;
        }
        if (!flag && isTracked(file, HelperMethods.readHEADAsCommit())) {
            removed.add(f);
            restrictedDelete(f);
            flag = true;
        }
        save();
        return flag;
    }

    /**
     * 在执行 gitlet-commit 操作后，更新索引
     * 包括added, removed, tracked,然后保存索引
     */
    public void cleanStagingArea() {
        added.clear();
        removed.clear();
        tracked.clear();
        save();
    }

    /**
     * Write index object.
     */
    public void save() {
        Utils.writeObject(Repository.INDEX, this);
    }

    /**
     * Test file is whether removed or not.
     */
    public boolean isRemoved(File infile) {
        return removed.contains(infile.getAbsolutePath());
    }

    /**
     * Test file is whether staged or not.
     */
    public boolean isStaged(File infile) {
        return added.containsKey(infile.getAbsolutePath());
    }

    /**
     * Test whether given file is tracked but not commit.
     */
    private boolean isTracked(File infile) {
        return tracked.contains(infile.getAbsolutePath());
    }

    /**
     * Test whether given file is tracked.
     */
    public boolean isTracked(File file, Commit c) {
        return c.getBlob(file) != null || isTracked(file);
    }

    public boolean isCommited() {
        return added.isEmpty() && removed.isEmpty();
    }

    /**
     * getters
     */
    public Map<String, String> getAdded() {
        return added;
    }

    public Set<String> getRemoved() {
        return removed;
    }

    /**
     * 在 staged area 中得到文件名称
     */
    public Set<String> getAddedFilenames() {
        HashSet<String> ret = new HashSet<>();
        added.keySet().forEach(n -> ret.add(join(n).getName()));
        return ret;
    }

    /**
     * 在 filenames area 中得到文件名称
     */
    public Set<String> getRemovedFilenames() {
        HashSet<String> ret = new HashSet<>();
        removed.forEach(n -> ret.add(join(n).getName()));
        return ret;
    }

}
