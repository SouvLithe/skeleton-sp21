package gitlet;


import java.io.File;
import java.io.Serializable;
// 在频繁使用某个类的静态方法时使用这个东西
import static gitlet.Utils.*;

/**
 * 表示一个对于gitlet commit对象的 blob 对象
 *
 * @author SouvLithe
 */
public class Blob implements Serializable {

    /**
     * 该文件 blob 内容所指向的内容。
     */
    private final String content;

    /**
     * blob对象的 SHA-1 id
     */
    private final String uid;

    /**
     * 使用文件实例化一个 blob 对象。
     * blob 指的是对已跟踪文件的快照。
     */
    public Blob(File file) {
        this.content = readContentsAsString(file);
        this.uid = getBlobName(file);
    }

    /**
     * 使用文件内容和文件名创建 Blob 标识。
     *
     * @return The blob SHA-1 id
     */
    public static String getBlobName(File file) {
        return sha1(readContentsAsString(file) + file.getName());
    }

    /**
     * 将 Blob 写入对象形式并保存至 TEMP_DIR 目录。
     *
     * @return 一个 40 位长度的唯一标识符的二进制数据块
     */
    public String makeBlob() {
        File out = Repository.makeObjectDir(this.uid);
        writeObject(out, this);
        return this.uid;
    }

    public String getContent() {
        return content;
    }
}
