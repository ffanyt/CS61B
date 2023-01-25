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
    private String timestamp;
    private String Hashcode;
    private List<String> parentNoed;
    private HashMap<String, String> BlobNode;
    public Commit() {
        message = "initial commit";
        BlobNode = new HashMap<>();
        Date time = new Date(0);
        timestamp = caltimestamp(time);
        parentNoed = new ArrayList<>();
        Hashcode = this.calHash();
    }
    public Commit(String ms) {
        message = ms;
        Date time = new Date();
        timestamp = caltimestamp(time);
        parentNoed = new ArrayList<>();
        parentNoed.add(Repository.HEAD);
        HashMap parentBlob = readParentBlob();
        BlobNode = updateBlob(parentBlob);
        Hashcode = calHash();
    }
    public void save() {
        File current_commit_File = join(Repository.Commit_DIR, this.Hashcode);
        writeObject(current_commit_File, this);
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
        File parentCommitFILE = getCommitFILE(parent);
        Commit parentCommit = readObject(parentCommitFILE, Commit.class);
        HashMap parentBlob = parentCommit.BlobNode;
        return parentBlob;
    }
    private File getCommitFILE(String hashcode) {
        return join(Repository.Commit_DIR, hashcode);
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
            newMap.remove(removeStageFile);
        }
        return newMap;
    }
    private String caltimestamp(Date time) {
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
