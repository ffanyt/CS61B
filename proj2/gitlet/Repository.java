package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;



/** Represents a gitlet repository.
 *
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    public static final File STAGE_DIR = join(GITLET_DIR, "stage");
    public static final File BRANCH_DIR = join(GITLET_DIR, "branch");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    public static final File REMOVEL_DIR = join(GITLET_DIR, "remove");
    private static Commit currentCommit;
    private static String HEAD;
    public static void init() {
        if (GITLET_DIR.exists()) {
            printError("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            STAGE_DIR.mkdir();
            BLOB_DIR.mkdir();
            BRANCH_DIR.mkdir();
            COMMIT_DIR.mkdir();
            REMOVEL_DIR.mkdir();
            initCommit();
            initHEAD();
            initBranch();
        }
    }
    public static void initBranch() {
        File master = join(BRANCH_DIR, "master");
        writeContents(master, currentCommit.getHashcode());
    }
    public static void initHEAD() {
        HEAD = currentCommit.getHashcode();
        writeObject(HEAD_FILE, HEAD);
    }
    public static void initCommit() {
        currentCommit = new Commit();
        currentCommit.save();
    }
    public static void add(String file) {
        checkinilization();
        HEAD = readHEAD();
        File currentFile = Repository.getWorkingFile(file);
        if (!currentFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Stage stageFile = new Stage(file);
        stageFile.save();
    }
    public static File getWorkingFile(String file) {
        return join(CWD, file);
    }
    public static void commit(String ms) {
        checkinilization();
        if (ms.equals("")) {
            printError("Please enter a commit message.");
        }
        List stageList = plainFilenamesIn(STAGE_DIR);
        List removeStageList = plainFilenamesIn(REMOVEL_DIR);
        if (stageList.size() == 0 && removeStageList.size() == 0) {
            printError("No changes added to the commit.");
        }
        HEAD = readHEAD();
        Commit cm = new Commit(ms);
        cm.save();
        String newHEAD = cm.getHashcode();
        updateBranch(newHEAD);
        updateHEAD(newHEAD);
        deletStage();
    }
    public static void rm(String file) {
        HEAD = readHEAD();
        File workingFILE = getWorkingFile(file);
        File stageFILE = Stage.getStageFile(file);

        Commit curCommit = Commit.readCommit(HEAD);
        HashMap blobNode = curCommit.getBlob();
        if (blobNode.containsKey(file)) {
            if (stageFILE.exists()) {
                Stage removalFILE = readObject(stageFILE, Stage.class);
                removalFILE.saveRemove();
                stageFILE.delete();
                if (workingFILE.exists()) {
                    workingFILE.delete();
                }
            } else {
                if (workingFILE.exists()) {
                    Stage removalFILE = new Stage(file);
                    removalFILE.saveRemove();
                    workingFILE.delete();
                } else {
                    Object rmBlobName = blobNode.get(file);
                    Blob rmBlob = Blob.readBlob(rmBlobName.toString());
                    Stage rmStage = new Stage(rmBlob);
                    rmStage.saveRemove();
                }
            }
        } else {
            if (stageFILE.exists()) {
                stageFILE.delete();
            } else {
                System.out.println("No reason to remove the file.");
                System.exit(0);
            }
        }
    }
    public static void log() {
        HEAD = readHEAD();
        Commit curCommit = Commit.readCommit(HEAD);
        Commit tempCommit = curCommit;
        while (true) {
            printLog(tempCommit);
            if (tempCommit.getParent().size() == 0) {
                break;
            }
            List parentList = tempCommit.getParent();
            Object parent1 = parentList.get(0);
            String parentNode = parent1.toString();
            tempCommit = Commit.readCommit(parentNode);
        }
    }
    public static void globalLog() {
        printORfind("print", "");
    }
    public static void find(String ms) {
        printORfind("find", ms);
    }
    public static void status() {
        HEAD = readHEAD();
        //String temp = HEAD;
        //System.out.println(temp);
        Commit curCommit = Commit.readCommit(HEAD);
        System.out.println("=== Branches ===");
        List branchList = plainFilenamesIn(BRANCH_DIR);
        if (branchList.size() != 0) {
            for (Object i : branchList) {
                String branchName = i.toString();
                File branchDir = join(BRANCH_DIR, branchName);
                String branchHeadHash = readContentsAsString(branchDir);
                //String branchHeadHash = readObject(branchDir, String.class);
                if (HEAD.equals(branchHeadHash)) {
                    System.out.println("*" + branchName);
                    break;
                }
            }
            for (Object i : branchList) {
                String branchName = i.toString();
                File branchDir = join(BRANCH_DIR, branchName);
                String branchHeadHash = readContentsAsString(branchDir);
                if (!HEAD.equals(branchHeadHash)) {
                    System.out.println(branchName);
                }
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        List stageList = plainFilenamesIn(STAGE_DIR);
        if (stageList.size() != 0) {
            for (Object i : stageList) {
                String stageName = i.toString();
                System.out.println(stageName);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        List removeList = plainFilenamesIn(REMOVEL_DIR);
        if (removeList.size() != 0) {
            for (Object i : removeList) {
                String removeName = i.toString();
                System.out.println(removeName);
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    public static void checkout(String cm, int select) {
        HEAD = readHEAD();
        if (select == 0) {
            chechBranchExit(cm);
            File branchFILE = join(BRANCH_DIR, cm);
            String branchInfo = readObject(branchFILE, String.class);
            if (branchInfo.equals(HEAD)) {
                printError("No need to checkout the current branch.");
            }
            updateWorkingdirByCommit(branchInfo);
        } else {
            Commit curCommit = Commit.readCommit(HEAD);
            rewriteFileByCommit(curCommit, cm);
            deleteStageFile(cm);
        }
    }
    public static void branch(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (branchFile.exists()) {
            printError("A branch with that name already exists.");
        }
        writeObject(branchFile, HEAD);
    }
    private static void writeBlob2File(Blob blob) {
        byte[] content = blob.getContent();
        File dir = join(CWD, blob.getFileName());
        writeContents(dir, new String(content, StandardCharsets.UTF_8));
    }
    public static void checkout(String cm1, String cm2) {
        Commit commit = checkCommitID(cm1);
        rewriteFileByCommit(commit, cm2);
        deleteStageFile(cm2);
    }
    private static Commit checkCommitID(String cmID) {
        List commitList = plainFilenamesIn(COMMIT_DIR);
        boolean flag = false;
        Commit commit = new Commit();
        for (Object i : commitList) {
            String commitName = i.toString();
            int len = cmID.length();
            String subCommitName = commitName.substring(0, len);
            if (cmID.equals(subCommitName)) {
                commit = Commit.readCommit(commitName);
                flag = true;
                break;
            }
        }
        if (!flag) {
            printError("No commit with that id exists.");
        }
        return commit;
    }
    public static void rmBranch(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            printError("A branch with that name does not exist.");
        }
        String branchHash = readObject(branchFile, String.class);
        if (branchHash.equals(HEAD)) {
            printError("Cannot remove the current branch.");
        }
        branchFile.delete();
    }
    public static void reset(String commitID) {
        HEAD = readHEAD();
        chechCommitExit(commitID);
        updateBranch(commitID);
        updateWorkingdirByCommit(commitID);
    }
    private static void chechCommitExit(String commitID) {
        File dir = join(COMMIT_DIR, commitID);
        if (!dir.exists()) {
            printError("No commit with that id exists.");
        }
    }
    private static void chechBranchExit(String branchName) {
        boolean flag = false;
        List branchList = plainFilenamesIn(BRANCH_DIR);
        for (Object i : branchList) {
            String branchFile = i.toString();
            if (branchFile.equals(branchName)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            printError("No such branch exists.");
        }
    }
    private static void updateWorkingdirByCommit(String newCommitID) { ////bug
        Commit newCommit = Commit.readCommit(newCommitID);
        Commit curCommit = Commit.readCommit(HEAD);
        HashMap commitBlobNode = newCommit.getBlob();
        HashMap currentBlobNode = curCommit.getBlob();
        for (Object key : commitBlobNode.keySet()) {
            String keyString = key.toString();
            if (!currentBlobNode.containsKey(keyString)) {
                File dir = join(CWD, keyString);
                if (dir.exists()) {
                    printError("There is an untracked file in the way; "
                            +
                            "delete it, or add and commit it first.");
                }
            }
        }
        for (Object key : currentBlobNode.keySet()) {
            if (!commitBlobNode.containsKey(key.toString())) {
                File dir = join(CWD, key.toString());
                dir.delete();
            }
        }
        for (Object key : commitBlobNode.keySet()) {
            String keyString = key.toString();
            Object blobHash = commitBlobNode.get(keyString);
            String blobHashString = blobHash.toString();

            if (currentBlobNode.containsKey(keyString)) {
                Object hash = currentBlobNode.get(keyString);
                if (hash.equals(blobHash)) {
                    continue;
                } else {
                    Blob newblob = Blob.readBlob(blobHashString);
                    writeBlob2File(newblob);
                }
            } else  {
                Blob newblob = Blob.readBlob(blobHashString);
                writeBlob2File(newblob);
            }
        }
        updateHEAD(newCommit.getHashcode());
    }
    private static void rewriteFileByCommit(Commit cm, String fileName) {
        HashMap currentBlobNode = cm.getBlob();
        if (!currentBlobNode.containsKey(fileName)) {
            printError("File does not exist in that commit.");
        }
        Object currentFileHash = currentBlobNode.get(fileName);
        File currentBlobFile = join(BLOB_DIR, currentFileHash.toString());
        Blob currentBlob = readObject(currentBlobFile, Blob.class);
        writeBlob2File(currentBlob);
    }
    private static void deleteStageFile(String file) {
        File dir = join(STAGE_DIR, file);
        if (!dir.exists()) {
            return;
        }
        dir.delete();
    }
    private static void printORfind(String cmd, String ms) {
        List commitList = plainFilenamesIn(COMMIT_DIR);
        File commitFILE;
        Commit commit;
        boolean found = false;
        for (Object i : commitList) {
            commitFILE = join(COMMIT_DIR, i.toString());
            commit = readObject(commitFILE, Commit.class);
            if (cmd.equals("print")) {
                printLog(commit);
            } else if (cmd.equals("find")) {
                String commitMs = commit.getMessage();
                if (commitMs.equals(ms)) {
                    System.out.println(commit.getHashcode());
                    found = true;
                }
            }
        }
        if (cmd.equals("find") && !found) {
            printError("Found no commit with that message.");
        }
    }

    private static void printLog(Commit printcommit) {
        System.out.println("===");
        System.out.println("commit " + printcommit.getHashcode());
        System.out.println("Date: " + printcommit.getTimestamp());
        System.out.println(printcommit.getMessage());
        System.out.println();
    }

    public static boolean checkinilization() {
        if (!GITLET_DIR.exists()) {
            printError("Not in an initialized Gitlet directory.");
        }
        return true;
    }
    private static String readHEAD() {
        return readObject(HEAD_FILE, String.class);
    }
    private static void updateBranch(String hashCode) {
        List branchList = plainFilenamesIn(BRANCH_DIR);
        for (Object i : branchList) {
            File branchFILE = join(BRANCH_DIR, i.toString());
            String content = readContentsAsString(branchFILE);
            if (content.equals(HEAD)) {
                writeContents(branchFILE, hashCode);
            }
        }
    }
    private static void updateHEAD(String hashCode) {
        HEAD = hashCode;
        writeObject(HEAD_FILE, HEAD);
    }
    private static String readMaster() {
        return readObject(join(BRANCH_DIR, "master"), String.class);
    }
    private static void deletStage() {
        File stageDIR = STAGE_DIR;
        List file = plainFilenamesIn(stageDIR);
        for (Object i : file) {
            File fileDIR = join(stageDIR, i.toString());
            fileDIR.delete();
        }
    }
    public static void printError(String words) {
        System.out.println(words);
        System.exit(0);
    }
    public static String getHEAD() {
        return HEAD;
    }
}
