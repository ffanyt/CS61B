package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;
public class Stage implements Serializable {
    private String fileName;
//    private File stageFile;
//    //private byte[] content;
//    private String contentAsString;
//    private String HashCode;
    public Stage() {
        fileName = null;
        //stageFile = null;
    }
    public Stage(String file) {
        File workingFILE = Repository.getWorkingFile(file);
        byte[] content = readContents(workingFILE);
        //contentAsString = content.toString();
        fileName = file;
        //stageFile = getStageFile(fileName);
        //HashCode = getHashCode();
    }
    public void save() {
//        File commitFile = getBlobFile(this.HashCode);
//        if (commitFile.exists()) {
//            if (this.stageFile.exists()) {
//                this.stageFile.delete();
//            } else {
//                return;
//            }
//        } else {
//            writeObject(this.stageFile, this);
////        }
//        writeObject(Repository.STAGE_DIR, );
    }
    public static File getStageFile(String file) {
        return join(Repository.STAGE_DIR, file);
    }
    private File getBlobFile(String file) {
        return join(Repository.BLOB_DIR, file);
    }
    public String getHashCode() {
//        return sha1(this.fileName, this.contentAsString);
        return "123";
    }
//    public byte[] getContent() {
//        return content;
//    }
    public Stage readStage(String file) {
        File DIR = join(Repository.STAGE_DIR, file);
        Stage a = readObject(DIR, Stage.class);
        return a;
    }
}
