package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;
public class Stage implements Serializable {
    private String fileName;
    private File stageFile;
    private byte[] content;
//    private String contentAsString;
    private String hashCode;
    public Stage(String file) {
        File workingFILE = Repository.getWorkingFile(file);
//        System.out.println("the dir:" + workingFILE);
        this.content = readContents(workingFILE);
//        String a = new String(content, StandardCharsets.UTF_8);
//        System.out.println(a);
//        contentAsString = content.toString();
        fileName = file;
        stageFile = getStageFile(fileName);
        hashCode = getHashCode(this.fileName, this.content);
    }
    public Stage(String file, String txt) {
        fileName = file;
        stageFile = getStageFile(fileName);
        content = txt.getBytes();
        hashCode = getHashCode(this.fileName, this.content);
    }
    public Stage(Blob blob) {
        this.content = blob.getContent();
        this.hashCode = blob.getHashCode();
        this.fileName = blob.getFileName();
        this.stageFile = getStageFile(this.fileName);
    }
    public void save() {
        boolean flag = false;
        File blobFile = getBlobFile(this.hashCode);
        if (blobFile.exists()) {
            String head = Repository.getHEAD();
            Commit currentCommit = Commit.readCommit(head); //获取当前commit
            HashMap currentCommitBlob = currentCommit.getBlob(); //获取当前commit关系到的Blob文件
            if (currentCommitBlob.containsKey(this.fileName)) { //查看当前commit是否存有当前文件名的文件
                if (currentCommitBlob.containsValue(this.hashCode)) {
                    //如果和当前Hash值相同，说明现在add的文件和commit版本相同
                    if (this.stageFile.exists()) {
                        this.stageFile.delete();
                        flag = true;
                    } else {
                        flag = true;
                    }
                }
            }
        }
        if (!flag) {
            writeObject(this.stageFile, this);
        }
        List removeStageList = plainFilenamesIn(Repository.REMOVEL_DIR);
        for (Object i : removeStageList) {
            String rmStageName = i.toString();
            if (rmStageName.equals(this.fileName)) {
                File rmStageFile = join(Repository.REMOVEL_DIR, rmStageName);
                rmStageFile.delete();
            }
        }
    }
    public void saveRemove() {
        File removeFILE = join(Repository.REMOVEL_DIR, this.fileName);
        writeObject(removeFILE, this);
    }
    public static File getStageFile(String file) {
        return join(Repository.STAGE_DIR, file);
    }
    private File getBlobFile(String file) {
        return join(Repository.BLOB_DIR, file);
    }
    private static String getHashCode(String fileName, byte[] content) {
        return sha1(fileName, content);
    }
    public String getHashCode() {
        return this.hashCode;
    }
    public byte[] getContent() {
        return content;
    }
}
