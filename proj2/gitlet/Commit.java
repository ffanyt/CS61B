package gitlet;

// TODO: any imports you need here

//import edu.princeton.cs.algs4.BST;

import java.io.File;
import java.io.Serializable;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    //private Date time;
    private String timestamp;
    private String Hashcode;
    private List<String> parentNoed;
    private HashMap<String, String> BlobNode;
    public Commit() {
        message = "initial commit";
        BlobNode = new HashMap<>();
        Date time = new Date(0);
        timestamp = time.toString();
        parentNoed = new ArrayList<>();
        Hashcode = this.calHash();
    }
    public Commit(String ms) {
        message = ms;
        timestamp = caltimestamp();
        parentNoed = new ArrayList<>();
        parentNoed.add(Repository.HEAD);
        HashMap parentBlob = readParentBlob();//读取父commit中的BlobNode
        BlobNode = updateBlob(parentBlob);//通过父commit中BlobNode和stage区文件的对比，得到该commit的BlobNode
        Hashcode = calHash();
        System.out.println("当前的Commit信息：");
        System.out.println("timestamp：" + timestamp);
        System.out.println("parentNode："+parentNoed.toString());
        System.out.println("BlobNode："+BlobNode.toString());
    }
    public void save() {
        File current_commit_File = join(Repository.Commit_DIR, this.Hashcode);
        writeObject(current_commit_File, this);
        System.out.println("存储commit信息");
    }
    public String getHashcode() {
        return Hashcode;
    }
    private File getFileName() {
        return join(Repository.Commit_DIR, Hashcode);
    }
    private String calHash() {
        String code = sha1(message, timestamp, parentNoed.toString(), BlobNode.toString());
        return code;
    }
    private HashMap readParentBlob() {
        String parent = Repository.HEAD;
        File parentCommitFILE = getCommitFILE(parent);//获得父commit的路径
        Commit parentCommit = readObject(parentCommitFILE, Commit.class);//读取父commit
        HashMap parentBlob = parentCommit.BlobNode;//读取父commit中的BlobNode
        return parentBlob;
    }
    private File getCommitFILE(String hashcode) {
        return join(Repository.Commit_DIR, hashcode);
    }
    private HashMap updateBlob(HashMap parent) {
        HashMap newMap = parent;
        List stageList = plainFilenamesIn(Repository.STAGE_DIR);
        for (Object i : stageList) {//遍历stage中的文件
            String stageFILENAME = i.toString();
            if (parent.containsKey(stageFILENAME)) {//如果父BlobNode有存这个文件的名字，说明已经存有文件的旧版本
                Blob newBlob = new Blob(stageFILENAME);//把文件新版本转化为Blob存在Blob文件夹中
                newBlob.save();
                newMap.replace(stageFILENAME, newBlob.hashCode());//将新BlobNode的文件对应的Hashcode换成刚刚存的Blob
            } else {//父BlobNode没有这个文件的名字
                Blob newBlob = new Blob(stageFILENAME);
                newBlob.save();//把这个文件以Blob形式存起来
                newMap.put(stageFILENAME, newBlob.hashCode());//直接把新的文件名和Blob的Hashcode存在BlobNode中
            }
        }
        //开始更新remove信息
        List removeStageList = plainFilenamesIn(Repository.REMOVEL_DIR);
        for (Object i : removeStageList) {
            String removeStageFile = i.toString();//遍历removal文件夹中文件名
            newMap.remove(removeStageFile);//将文件名对应的节点删除
        }
        return newMap;
    }
    private String caltimestamp() {
        Date time = new Date();
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(time);
    }
    public static Commit readCommit(String file) {
        File commitFILE = join(Repository.Commit_DIR, file);
        if (!commitFILE.exists()) {
            Repository.printError("No commit with that id exists.");
        }
        return readObject(commitFILE, Commit.class);
    }
    public HashMap getBlob() {
        return BlobNode;
    }
    public List getParent() {
        return parentNoed;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public String getMessage() {
        return message;
    }
}
