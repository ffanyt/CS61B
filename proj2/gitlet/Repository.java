package gitlet;

import edu.princeton.cs.algs4.Queue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    public static final File HEAD_BRANCH_FILE = join(GITLET_DIR, "HEAD_BRANCH");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    public static final File REMOVEL_DIR = join(GITLET_DIR, "remove");
    private static Commit currentCommit;
    private static String HEAD;
    private static String HEAD_BRANCH;
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
        HEAD_BRANCH = "master";
        writeContents(HEAD_BRANCH_FILE, HEAD_BRANCH);
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
        HEAD_BRANCH = readContentsAsString(HEAD_BRANCH_FILE);
        Commit cm = new Commit(ms);
        cm.save();
        String newHEAD = cm.getHashcode();
        updateBranch(newHEAD);
        updateHEAD(newHEAD);
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
        HEAD_BRANCH = readContentsAsString(HEAD_BRANCH_FILE);
        Commit curCommit = Commit.readCommit(HEAD);
        System.out.println("=== Branches ===");
        List branchList = plainFilenamesIn(BRANCH_DIR);
        if (branchList.size() != 0) {
            System.out.println("*" + HEAD_BRANCH);
            for (Object i : branchList) {
                String branchName = i.toString();
                File branchDir = join(BRANCH_DIR, branchName);
                String branchHeadHash = readContentsAsString(branchDir);
                if (!branchName.equals(HEAD_BRANCH)) {
                    System.out.println(branchName);
                }
//                if (!HEAD.equals(branchHeadHash)) {
//                    System.out.println(branchName);
//                }
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
            checkCurrentBranch(cm);
            File branchFILE = join(BRANCH_DIR, cm);
            String branchInfo = readContentsAsString(branchFILE);
            updateWorkingdirByCommit(branchInfo);
            updateHeadBranch(cm);
        } else {
            Commit curCommit = Commit.readCommit(HEAD);
            rewriteFileByCommit(curCommit, cm);
            deleteStageFile(cm);
        }
    }
    public static void branch(String branchName) {
        HEAD = readHEAD();
        File branchFile = join(BRANCH_DIR, branchName);
        if (branchFile.exists()) {
            printError("A branch with that name already exists.");
        }
        writeContents(branchFile, HEAD);
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
        HEAD_BRANCH = readContentsAsString(HEAD_BRANCH_FILE);
        if (branchName.equals(HEAD_BRANCH)) {
            printError("Cannot remove the current branch.");
        }
        branchFile = join(BRANCH_DIR, branchName);
        branchFile.delete();
    }
    public static void reset(String commitID) { //要把对应的branch指示的头提前
        HEAD = readHEAD();
        String fullCommitID = updateBranch(commitID);
        updateWorkingdirByCommit(fullCommitID);
        List stageList = plainFilenamesIn(STAGE_DIR);
        for (Object i : stageList) {
            File stageFile = join(STAGE_DIR, i.toString());
            stageFile.delete();
        }
        updateHEAD(fullCommitID);
    }
    private static void checkCurrentBranch(String branchName) {
        HEAD_BRANCH = readContentsAsString(HEAD_BRANCH_FILE);
        if (branchName.equals(HEAD_BRANCH)) {
            printError("No need to checkout the current branch.");
        }
    }
    private static void chechBranchExit(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            printError("No such branch exists.");
        }
    }
    private static void updateWorkingdirByCommit(String newCommitID) {
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
    private static void updateHeadBranch(String branchName) {
        HEAD_BRANCH = branchName;
        writeContents(HEAD_BRANCH_FILE, HEAD_BRANCH);
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
    private static String updateBranch(String hashCode) {
        List branchList = plainFilenamesIn(BRANCH_DIR);
        HEAD_BRANCH = readContentsAsString(HEAD_BRANCH_FILE);
        for (Object i : branchList) {
            String name = i.toString();
            File branchFILE = join(BRANCH_DIR, name);
            if (name.equals(HEAD_BRANCH)) {
                if (hashCode.length() < 40) {
                    Commit commit = checkCommitID(hashCode);
                    String fullHashCode = commit.getHashcode();
                    writeContents(branchFILE, fullHashCode);
                    return fullHashCode;
                } else {
                    writeContents(branchFILE, hashCode);
                    return hashCode;
                }

            }
        }
        return "";
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
    public static void merge(String branchName) {
        //找分裂点
        checkStageEmpty();
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            printError("A branch with that name does not exist.");
        }
        HEAD = readHEAD();
        HEAD_BRANCH = readHeadBranch();
        if (HEAD_BRANCH.equals(branchName)) {
            printError("Cannot merge a branch with itself.");
        }
        String curHash = readHashByBrchNm(HEAD_BRANCH);
        Commit commiHead = Commit.readCommit(curHash);
        String givenHash = readHashByBrchNm(branchName);
        Commit commitOther = Commit.readCommit(givenHash);
        Commit commitSplit = findSplitPoint(commiHead, commitOther);
        checkIf2SplitDiffHead(commitSplit, commiHead, branchName);
        checkIfSplitDiffOther(commitSplit, commitOther);
        String message = "Merged " + branchName + " into " + HEAD_BRANCH + ".";
        Commit mergeCommit = merge2NewCommit(commitSplit, commiHead, commitOther, message);

        // GET all file name
        //List allFileName = calAllFile(commitSplit, commiHead, commitOther);

        mergeCommit.save();
        String temp = HEAD_BRANCH;
        File curBranchFile = join(BRANCH_DIR, HEAD_BRANCH);
        checkout(branchName, 0);
        writeContents(curBranchFile, mergeCommit.getHashcode());
        checkout(temp, 0);
        //updateHEAD(mergeCommit.getHashcode());
    }
    private static Commit merge2NewCommit(Commit split, Commit head, Commit other, String ms) {
        List allFileName = calAllFile(split, head, other);
        String headHash = head.getHashcode();
        String otherHash = other.getHashcode();
        List parent = new ArrayList<>(); //bug
        parent.add(headHash);
        parent.add(otherHash);
        updStg(split, head, other, allFileName);
        Commit mergeCommit = new Commit(ms, parent);
        return mergeCommit;
    }
    private static void updStg(Commit sp, Commit he,
                               Commit ot, List alFi) {
        HashMap splitBlob = sp.getBlob();
        HashMap headBlob = he.getBlob();
        HashMap otherBlob = ot.getBlob();
        HashMap mergeBlob = headBlob;
        boolean splitflag = false;
        boolean headflag = false;
        boolean otherflag = false;
        Object splitFileHash = null;
        Object headFileHash = null;
        Object otherFileHash = null;
        boolean flag = false;
        for (Object i : alFi) {
            splitflag = false;
            headflag = false;
            otherflag = false;
            String fileName = i.toString();
            if (splitBlob.containsKey(fileName)) {
                splitflag = true;
                splitFileHash = splitBlob.get(fileName);
            }
            if (headBlob.containsKey(fileName)) {
                headflag = true;
                headFileHash = headBlob.get(fileName);
            }
            if (otherBlob.containsKey(fileName)) {
                otherflag = true;
                otherFileHash = otherBlob.get(fileName);
            }
            if (splitflag) {
                if (headflag) {
                    if (otherflag) {
                        if (splitFileHash.equals(headFileHash)
                                &&
                                !splitFileHash.equals(otherFileHash)) { //case1
                            stage(otherFileHash.toString()); //把other的文件stage
                        } else if (!splitFileHash.equals(headFileHash)
                                &&
                                splitFileHash.equals(otherFileHash)) { //case2
                            continue;
                        } else {
                            if (!flag) {
                                System.out.println("Encountered a merge conflict.");
                                flag = true;
                            }
                            conflict(headFileHash.toString(), otherFileHash.toString(), true, true);
                        }
                    } else {
                        if (splitFileHash.equals(headFileHash)) {
                            rmStage(headFileHash.toString()); //case4 rm
                        } else {
                            if (!flag) {
                                System.out.println("Encountered a merge conflict.");
                                flag = true;
                            }
                            conflict(headFileHash.toString(), "", true, false);
                        }
                    }
                } else { //case3 5
                    if (otherflag) {
                        if (splitFileHash.equals(otherFileHash)) {
                            continue;
                        } else {
                            if (!flag) {
                                System.out.println("Encountered a merge conflict.");
                                flag = true;
                                conflict("", otherFileHash.toString(), false, true);
                            }
                            stage(otherFileHash.toString());
                        }
                    } else {
                        continue;
                    }
                    continue;
                }
            } else {
                if (headflag) {
                    if (otherflag) {
                        //麻烦
                        if (headFileHash.equals(otherFileHash)) {
                            continue;
                        } else {
                            if (!flag) {
                                System.out.println("Encountered a merge conflict.");
                                flag = true;
                            }
                            conflict(headFileHash.toString(), otherFileHash.toString(), true, true);
                        }
                    } else {
                        continue;
                    }
                } else {
                    //case6
                    stage(otherFileHash.toString());
                }
            }
        }
    }
    private static void stage(String blobHash) {
        Blob blob = Blob.readBlob(blobHash);
        Stage stage = new Stage(blob);
        stage.save();
    }
    private static void rmStage(String blobHash) {
        Blob blob = Blob.readBlob(blobHash);
        Stage stage = new Stage(blob);
        stage.saveRemove();
    }
    private static void conflict(String headBlobHash,
                                 String otherBlobHash, boolean head, boolean other) {
        String headContent;
        String otherContent;
        Blob headBlob = null;
        Blob otherBlob = null;
        if (head) {
            headBlob = Blob.readBlob(headBlobHash);
            byte[] headBytes = headBlob.getContent();
            headContent = new String(headBytes, StandardCharsets.UTF_8);
        } else {
            headContent = headBlobHash;
        }
        if (other) {
            otherBlob = Blob.readBlob(otherBlobHash);
            byte[] otherBytes = otherBlob.getContent();
            otherContent = new String(otherBytes, StandardCharsets.UTF_8);
        } else {
            otherContent = otherBlobHash;
        }
        String result = "<<<<<<< HEAD\n" + headContent + "=======\n"
                + otherContent + ">>>>>>>\n";
        if (head) {
            Stage a = new Stage(headBlob.getFileName(), result);
            a.save();
        } else {
            Stage a = new Stage(otherBlob.getFileName(), result);
            a.save();
        }

    }
    private static List calAllFile(Commit split, Commit head, Commit other) {
        List allFile = new ArrayList<String>();
        HashMap headBlob = head.getBlob();
        HashMap otherBlob = other.getBlob();
        HashMap splitBlob = split.getBlob();
        getFileFromBlob(headBlob, allFile);
        getFileFromBlob(otherBlob, allFile);
        getFileFromBlob(splitBlob, allFile);
        return allFile;
    }
    private static void checkIf2SplitDiffHead(Commit split, Commit head, String branchName) {
        if (split.getHashcode().equals(head.getHashcode())) {
            checkout(branchName, 0);
            printError("Current branch fast-forwarded.");
        }
    }
    private static void checkIfSplitDiffOther(Commit split, Commit given) {
        if (split.getHashcode().equals(given.getHashcode())) {
            printError("Given branch is an ancestor of the current branch.");
        }
    }
    private static Commit findSplitPoint(Commit curCommit, Commit givenCommit) {
        List curParentList = curCommit.getParent();
        String curHash = curCommit.getHashcode();
        HashMap curParentHashM = getParentMap(curCommit, curHash);
        List givenParentList = givenCommit.getParent();
        String givenHash = givenCommit.getHashcode();
        HashMap givenParentHashM = getParentMap(givenCommit, givenHash);
        Object splitHash = null;
        for (int i = 0; curParentHashM.containsKey(i); i++) {
            Object parent = curParentHashM.get(i);
            if (givenParentHashM.containsValue(parent.toString())) {
                splitHash = parent; //get split Node's hashcode
                break;
            }
        }
        Commit spliCommit = Commit.readCommit(splitHash.toString());
        return spliCommit;
    }
    private  static void checkStageEmpty() {
        List stageList = plainFilenamesIn(STAGE_DIR);
        List rmStageList = plainFilenamesIn(REMOVEL_DIR);
        if (stageList.size() != 0 || rmStageList.size() != 0) {
            printError("You have uncommitted changes.");
        }
    }
    private static void getFileFromBlob(HashMap blobNode, List list) {
        for (Object i : blobNode.keySet()) {
            String key = i.toString();
            if (!list.contains(key)) {
                list.add(key);
            }
        }
    }
    private static HashMap getParentMap(Commit curCommit, String cur) {
        int count = 0;
        Queue a = new Queue<String>();
        a.enqueue(curCommit.getHashcode());
        List parentList;
        HashMap parentHashMap = new HashMap<Integer, String>();
        parentHashMap.put(count, cur);
        while (!a.isEmpty()) {
            count += 1;
            Object b = a.dequeue();
            String hash = b.toString();
            Commit commit = Commit.readCommit(hash);
            parentList = commit.getParent();
            for (int i = 0; i < parentList.size(); i++) {
                Object parentID = parentList.get(i);
                parentHashMap.put(count, parentID.toString());
                a.enqueue(parentID);
            }

        }
//        while (parentList.size() != 0) {
//            count += 1;
//            parentList = a.poll().getParent();
//            for (int i = 0; i < parentList.size(); i++) {
//                Object parentID = parentList.get(i);
//                parentHashMap.put(count, parentID.toString());
//                Commit curParentCommit = Commit.readCommit(parentID.toString());
//                a.add(curParentCommit);
//                parentList = curParentCommit.getParent();
//            }
//
//        }
        return parentHashMap;
    }
    private static String readHeadBranch() {
        return readContentsAsString(HEAD_BRANCH_FILE);
    }
    private static String readHashByBrchNm(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        return readContentsAsString(branchFile);
    }
}
