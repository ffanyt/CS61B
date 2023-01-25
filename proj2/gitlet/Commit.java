package gitlet;


//import edu.princeton.cs.algs4.BST;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private String timestamp;
    private String hashCode;
    private List<String> parentNoed;
    private HashMap<String, String> blobNode;
    public Commit() {
        message = "initial commit";
        blobNode = new HashMap<>();
        Date time = new Date(0);
        timestamp = caltimestamp(time);
        parentNoed = new ArrayList<>();
        hashCode = this.calHash();
    }
    public Commit(String ms) {
        message = ms;
        Date time = new Date();
        timestamp = caltimestamp(time);
        parentNoed = new ArrayList<>();
        parentNoed.add(Repository.getHEAD());
        HashMap parentBlob = readParentBlob();
        blobNode = updateBlob(parentBlob);
        hashCode = calHash();
    }
    public void save() {
        File currentCommitFile = join(Repository.COMMIT_DIR, this.hashCode);
        writeObject(currentCommitFile, this);
    }
    public String getHashcode() {
        return hashCode;
    }
    private File getFileName() {
        return join(Repository.COMMIT_DIR, hashCode);
    }
    private String calHash() {
        String code = sha1(message, timestamp, parentNoed.toString(), blobNode.toString());
        return code;
    }
    private HashMap readParentBlob() {
        String parent = Repository.getHEAD();
        File parentCommitFILE = getCommitFILE(parent);
        Commit parentCommit = readObject(parentCommitFILE, Commit.class);
        HashMap parentBlob = parentCommit.blobNode;
        return parentBlob;
    }
    private File getCommitFILE(String hashcode) {
        return join(Repository.COMMIT_DIR, hashcode);
    }
    private HashMap updateBlob(HashMap parent) {
        HashMap newMap = parent;
        List stageList = plainFilenamesIn(Repository.STAGE_DIR);
        for (Object i : stageList) {
            String stageFILENAME = i.toString();
            if (parent.containsKey(stageFILENAME)) {
                Blob newBlob = new Blob(stageFILENAME);
                newBlob.save();
                newMap.replace(stageFILENAME, newBlob.getHashCode());
            } else {
                Blob newBlob = new Blob(stageFILENAME);
                newBlob.save();
                newMap.put(stageFILENAME, newBlob.getHashCode());
                //String filename = stageFILENAME;
                //System.out.println("hash:"+newBlob.getHashCode());
            }
        }

        List removeStageList = plainFilenamesIn(Repository.REMOVEL_DIR);
        for (Object i : removeStageList) {
            String removeStageFile = i.toString();
            if (newMap.containsKey(removeStageFile)) {
                newMap.remove(removeStageFile);
            }
        }
        Repository.REMOVEL_DIR.delete();
        return newMap;
    }
    private String caltimestamp(Date time) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(time);
    }
    public static Commit readCommit(String file) {
        File commitFILE = join(Repository.COMMIT_DIR, file);
        if (!commitFILE.exists()) {
            Repository.printError("No commit with that id exists.");
        }
        return readObject(commitFILE, Commit.class);
    }
    public HashMap getBlob() {
        return blobNode;
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
