package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

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
    public void save() {
        File blobFile = getBlobFile(this.hashCode);
        if (blobFile.exists()) {
            String head = Repository.getHEAD();
            Commit currentCommit = Commit.readCommit(head); //获取当前commit
            HashMap currentCommitBlob = currentCommit.getBlob(); //获取当前commit关系到的Blob文件
            if (currentCommitBlob.containsKey(this.fileName)) { //查看当前commit是否存有当前文件名的文件
//                System.out.println("this commit has the same name of this file");
//                Object b = currentCommitBlob.get(this.fileName);
//                System.out.println("the same name file of hash is:" + b.toString());
                if (currentCommitBlob.containsValue(this.hashCode)) {
                    //如果和当前Hash值相同，说明现在add的文件和commit版本相同
                    if (this.stageFile.exists()) {
                        this.stageFile.delete();
                        return;
                    } else {
                        return;
                    }
                }
            }
        }
        writeObject(this.stageFile, this);
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
