package gitlet;

//import edu.princeton.cs.algs4.ST;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String fileName;
    private File Blob_FILE;
    //private byte[] content;
    private String hashCode;
    private byte[] content;
    public Blob(String file) {
        this.fileName = file;
        File stageFILE = Stage.getStageFile(file);
        Stage newStageFILE = readObject(stageFILE, Stage.class);
        //this.content = newStageFILE.getContent();
        this.hashCode = newStageFILE.getHashCode();
        content = newStageFILE.getContent();
        Blob_FILE = getBlobtFile(this.hashCode);
    }
    public static File getBlobtFile(String file) {
        return join(Repository.BLOB_DIR, file);
    }
    public static Blob readBlob(String file) {
        File fileDir = getBlobtFile(file);
        return readObject(fileDir, Blob.class);
    }
    public void save() {
        writeObject(this.Blob_FILE, this);
    }

    public byte[] getContent() {
        return content;
    }
    public String getFileName() {
        return fileName;
    }
    public String getHashCode() {
        return this.hashCode;
    }
}
