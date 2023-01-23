package gitlet;

import javax.swing.text.Style;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
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
    public static final File HEAD_File = join(GITLET_DIR, "HEAD");
    public static final File Commit_DIR = join(GITLET_DIR, "commit");
    public static final File REMOVEL_DIR = join(GITLET_DIR, "remove");
    public static Commit current_commit;
    public static String HEAD;
    /* TODO: fill in the rest of this class. */
    public static void Init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        } else {
            GITLET_DIR.mkdir();
            STAGE_DIR.mkdir();
            BLOB_DIR.mkdir();
            BRANCH_DIR.mkdir();
            Commit_DIR.mkdir();
            REMOVEL_DIR.mkdir();
            Init_Commit();
            Init_HEAD();
            Init_Branch();
        }
    }
    public static void Init_Branch() {
        File master = join(BRANCH_DIR, "master");
        writeContents(master, current_commit.getHashcode());
    }
    public static void Init_HEAD() {
        HEAD = current_commit.getHashcode();
        writeObject(HEAD_File, HEAD);
    }
    public static void Init_Commit() {
        current_commit = new Commit();
        current_commit.save();
    }
    public static void add(String file) {
        checkinilization();
        File current_file = Repository.getWorkingFile(file);
        if (!current_file.exists()) {
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
        if (ms == "") {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        List stageList = plainFilenamesIn(STAGE_DIR);
        if (stageList.size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        HEAD = readHEAD();
        System.out.println("----------");
        System.out.println("读取HEAD，即commit前的HEAD：" + HEAD);
        Commit cm = new Commit(ms);
        cm.save();
        String newHEAD = cm.getHashcode();
        //updateBranch(newHEAD);
        updateHEAD(newHEAD);
        deletStage();//清空stage
    }
    public static void rm(String file) {
        HEAD = readHEAD();
        File workingFILE = getWorkingFile(file);
        File stageFILE = Stage.getStageFile(file);

        Commit currentCommit = Commit.readCommit(HEAD);//获得当前commit
        HashMap BlobNode = currentCommit.getBlob();//获得当前Blob文件
        if (BlobNode.containsKey(file)) {
            if (stageFILE.exists()) {
                Stage removalFILE = readObject(stageFILE, Stage.class);//从暂存区取出读取该文件
                removalFILE.saveRemove();//把该stage文件存到remove区
                stageFILE.delete();//暂存区清除
                if (workingFILE.exists()) {
                    workingFILE.delete();//工作目录删除该文件
                }
            } else {
                Stage removalFILE = new Stage(file);//把工作目录该文件建立Stage类
                removalFILE.saveRemove();//把该stage文件存到remove区
                if (workingFILE.exists()) {
                    workingFILE.delete();//工作目录删除该文件
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
        Commit currentCommit = Commit.readCommit(HEAD);
        Commit tempCommit = currentCommit;
        while (true) {
            printLog(tempCommit);
            if (tempCommit.getParent().size() == 0) {
                break;
            }
            String parentNode = currentCommit.getParent().get(0).toString();
            tempCommit = Commit.readCommit(parentNode);
        }
    }
    public static void global_log() {
        printORfind("print", "");
    }
    public static void find(String ms) {
        printORfind("find", ms);
    }
    public static void status() {
        HEAD = readHEAD();
        Commit currentCommit = Commit.readCommit(HEAD);
        //打印branch
        System.out.println("=== Branches ===");
        List branchList = plainFilenamesIn(BRANCH_DIR);
        for (Object i : branchList) {
            String branchName = i.toString();
            File branchDir = join(BRANCH_DIR, branchName);
            String branchHeadHash = readObject(branchDir, String.class);
            if (HEAD.equals(branchHeadHash)) {
                System.out.println("*" + branchName);
                break;
            }
        }
        for (Object i : branchList) {
            String branchName = i.toString();
            File branchDir = join(BRANCH_DIR, branchName);
            String branchHeadHash = readObject(branchDir, String.class);
            if (!HEAD.equals(branchHeadHash)) {
                System.out.println(branchName);
            }
        }
        //打印stage file
        System.out.println("=== Staged Files ===");
        List stageList = plainFilenamesIn(STAGE_DIR);
        for (Object i : stageList) {
            String stageName = i.toString();
            System.out.println(stageName);
        }
        System.out.println();
        //打印remove
        System.out.println("=== Removed Files ===");
        List removeList = plainFilenamesIn(REMOVEL_DIR);
        for (Object i : removeList) {
            String removeName = i.toString();
            System.out.println(removeName);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    public static void checkout(String cm, int select) {//select 0 cm为branchname， 1 cm为filename
        if (select == 0) {
            boolean flag = false;
            List branchList = plainFilenamesIn(BRANCH_DIR);
            for (Object i : branchList) {
                String branchName = i.toString();
                if (branchName == cm) {
                    flag = true;
                }
            }
            if (!flag) {
                printError("No such branch exists.");
            }
            File branchFILE = join(BRANCH_DIR, cm);
            String branchInfo = readObject(branchFILE, String.class);
            if (branchInfo == HEAD) {
                printError("No need to checkout the current branch.");
            }
            Commit newCommit = Commit.readCommit(branchInfo);
            Commit currentCommit = Commit.readCommit(HEAD);
            HashMap commitBlobNode = newCommit.getBlob();//获取跳转节点的Blob的hashmap
            HashMap currentBlobNode = currentCommit.getBlob();//获取当前节点的Blob的HashMap
            for (Object key : currentBlobNode.keySet()) {

            }
            for (Object key : commitBlobNode.keySet()) {
                String keyString = key.toString();
                if (!currentBlobNode.containsKey(keyString)) {
                    File dir = join(CWD, keyString);
                    if (dir.exists()) {
                        printError("There is an untracked file in the way; delete it, or add and commit it first.");
                    }
                }
            }
            for (Object key : currentBlobNode.keySet()) {
                if (!commitBlobNode.containsKey(key.toString())) {//跳转的commit不包含当前commit的文件
                    File dir = join(CWD, key.toString());
                    dir.delete();
                }
            }
            for (Object key : commitBlobNode.keySet()) {//遍历跳转节点的Blob
                String keyString = key.toString();//遍历Blob的HashMap的key，即所含有的文件名
                Object BlobHash = commitBlobNode.get(keyString);//获得跳转节点Blob对应的hash值
                String BlobHashString = BlobHash.toString();

                if (currentBlobNode.containsKey(keyString)) {
                    Object hash = currentBlobNode.get(keyString);//获取当前分支文件对应的哈希
                    if (hash.equals(BlobHash)) {
                        continue;
                    } else {
                        Blob newblob = Blob.readBlob(BlobHashString);//获得跳转节点的Blob
                        writeBlob2File(newblob);
                    }
                } else  {
                    Blob newblob = Blob.readBlob(BlobHashString);//获得跳转节点的Blob
                    writeBlob2File(newblob);
                }
            }
            updateHEAD(newCommit.getHashcode());
        } else {
            Commit currentCommit = Commit.readCommit(HEAD);//获取当前commit信息
            rewriteFileByCommit(currentCommit, cm);
            deleteStageFile(cm);
        }
    }
    public static void branch(String branchName) {

    }
    private static void writeBlob2File(Blob blob) {
        byte[] content = blob.getContent();
        File dir = join(CWD, blob.getFileName());
        writeObject(dir, new String(content, StandardCharsets.UTF_8));
    }
    public static void checkout(String cm1, String cm2) {
        List commitList = plainFilenamesIn(Commit_DIR);
        Commit commit = new Commit();
        boolean flag = false;
        for (Object i : commitList) {
            String commitName = i.toString();
            String subCommitName = commitName.substring(0, 6);
            if (cm1 == subCommitName) {
                commit = Commit.readCommit(commitName);
                flag = true;
            }
        }
        if (!flag) {
            printError("No commit with that id exists.");
        }
        rewriteFileByCommit(commit, cm2);
        deleteStageFile(cm2);
    }
    private static void rewriteFileByCommit(Commit cm, String fileName) {
        HashMap currentBlobNode = cm.getBlob();//从当前commit信息获得Blob的hashmap
        if (!currentBlobNode.containsKey(fileName)) {
            printError("File does not exist in that commit.");
        }
        Object currentFileHash = currentBlobNode.get(fileName);//从blob的hashmap中获取该文件名对应的哈希编码
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
        List commitList = plainFilenamesIn(Commit_DIR);
        File commitFILE;
        Commit commit;
        boolean found = false;
        for (Object i : commitList) {
            commitFILE = join(Commit_DIR, i.toString());
            commit = readObject(commitFILE, Commit.class);
            if (cmd == "print") {
                printLog(commit);
            } else if (cmd == "find") {
                String commitMs = commit.getMessage();
                if (commitMs == ms) {
                    System.out.println(commit.getHashcode());
                    found = true;
                }
            }
        }
        if (cmd == "find" && found == false) {
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
        return readObject(HEAD_File, String.class);
    }
    private static void updateBranch(String HashCode) {//未写完
        List branchList = plainFilenamesIn(BRANCH_DIR);
        for (Object i : branchList) {
            File breachFILE = join(BRANCH_DIR, i.toString());
            String content = readObject(breachFILE, String.class);
            if (content == HEAD) {
                writeContents(breachFILE, HashCode);
            }
        }
    }
    private static void updateHEAD(String HashCode) {
        HEAD = HashCode;
        writeObject(HEAD_File, HEAD);
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
        System.out.println("清空stage");
    }
    public static void printError(String words) {
        System.out.println(words);
        System.exit(0);
    }
}
