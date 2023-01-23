package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Utils.*;
public class Stage implements Serializable {
    private String fileName;
    private File stageFile;
    private byte[] content;
    private String contentAsString;
    private String HashCode;
    public Stage(String file) {
        File workingFILE = Repository.getWorkingFile(file);
        this.content = readContents(workingFILE);
        contentAsString = content.toString();
        fileName = file;
        stageFile = getStageFile(fileName);
        HashCode = getHashCode();
    }
    public void save() {//bug!!!!!
        File commitFile = getBlobFile(this.HashCode);
        if (commitFile.exists()) {
            Commit currentCommit = Commit.readCommit(Repository.HEAD);//获取当前commit
            HashMap currentCommitBlob = currentCommit.getBlob();//获取当前commit关系到的Blob文件
            if (currentCommitBlob.containsKey(this.fileName)) {//查看当前commit是否存有当前文件名的文件
                Object currentCommitObj = currentCommitBlob.get(this.fileName);//获取到当前commit的文件对应的Hash值
                String currentCommitHash = currentCommitObj.toString();
                if (currentCommitHash == this.HashCode) {//如果和当前Hash值相同，说明现在add的文件和commit版本相同
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
    public String getHashCode() {
        return sha1(this.fileName, this.contentAsString);
    }
    public Stage readStage(String file) {
        File DIR = join(Repository.STAGE_DIR, file);
        Stage a = readObject(DIR, Stage.class);
        return a;
    }

    public byte[] getContent() {
        return content;
    }
}
