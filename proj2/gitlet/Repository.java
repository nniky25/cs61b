package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Utils.writeContents;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    private static String branch = "master";
    private static String otherBranch;
    private static String currentbranch;
    //private static StagingArea Area = new StagingArea();

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The split file. */
    public static final File SPLIT = join(GITLET_DIR, "split");
    /** The commit directory. */
    public static final File COMMIT = join(GITLET_DIR, "commit");
    /** The blob directory. */
    public static final File BLOB = join(GITLET_DIR, "blob");
    /** The head file. */
    public static final File HEAD = join(GITLET_DIR, "head");
    /** The current branch file. */
    public static final File BRANCH = join(GITLET_DIR, "branch");
    /** The status file. */
    public static final File STATUS = join(GITLET_DIR, "status");
    /** The staging file. */
    public static final File STAGING = join(GITLET_DIR, "staging");


    /* TODO: fill in the rest of this class. */
    /** Full construct (basic) .gitlet/ -- top level for all persistent data.
     *              (basic) - split -- file containing the branch split commit hash.
     *              (basic) - commit/ -- director containing the Commit hash files.
     *              (basic) - blob/ -- director containing the Blobs of fileHash.
     *              (basic) - head -- file containing the head commit hash.
     *              (basic) - branch -- file containing current branch.
     *              (basic) - status -- fils containing status object.
     *              (basic) - staging -- file containing the Staging Area.*/
    /** Set StagingArea. */

    /** init basic construct. */
    public static void setupPersistence() throws IOException {
        if (!GITLET_DIR.exists()) {
            if (!GITLET_DIR.mkdir()) throw new IOException("fail to mkdir" + GITLET_DIR.getAbsolutePath());
            if (!SPLIT.createNewFile()) throw new IOException("fail to create" + SPLIT.getAbsolutePath());
            if (!COMMIT.mkdir()) throw new IOException("fail to mkdir" + COMMIT.getAbsolutePath());
            if (!BLOB.mkdir()) throw new IOException("fail to mkdir" + BLOB.getAbsolutePath());
            if (!HEAD.createNewFile()) throw new IOException("fail to create" + HEAD.getAbsolutePath());
            if (!BRANCH.createNewFile()) throw new IOException("fail to create" + BRANCH.getAbsolutePath());
            if (!STATUS.createNewFile()) throw new IOException("fail to create" + STATUS.getAbsolutePath());
            if (!STAGING.createNewFile()) throw new IOException("fail to create" + STAGING.getAbsolutePath());
        } else {
            throw error("A Gitlet version-control system already exists in the current directory.");
        }
        Instant now = Instant.now();;
        Commit init = new Commit("initial commit",  now, null);

        // Save new commit to COMMIT directory.
        updateCommit(init);

        // Set Area Object to STAGING file.
        StagingArea Area = new StagingArea();
        writeObject(STAGING, Area);

        // Set Status Object to STATUS file.
        Status status = new Status(branch);
        status.addBranch(branch);
        writeObject(STATUS, status);
    }

    /** Do add. */
    public static void add(String fileName) throws IOException {
        //Check Whether the file exists.
        File currentFile = join(CWD, fileName);
        if (!currentFile.exists()) {
            throw error("File does not exist.");
        }
        // Read File content as byte.
        byte[] fileContent = readContents(currentFile);

        // serialized fileContent and get hash.
        String fileHash = sha1(fileContent);

        // Update
        StagingArea Area = readObject(STAGING, StagingArea.class);
        /* Copy file to Staged for addition area if changed. */
        updateArea(fileName, fileHash, Area, fileContent);
    }

    /** Check, then update STAGING file and add new blob to BLOB directory. */
    private static void updateArea(String fileName, String fileHash, StagingArea currentArea, byte[] fileContent) throws IOException {
        /* Check the fileName if was added to headCommit and if the same content if added. */
        // Get headCommit Object and Status Object.
        String headHash = readContentsAsString(HEAD);
        File headFile = join(COMMIT, headHash);

        Commit headCommit = readObject(headFile, Commit.class);

        Status status = readObject(STATUS, Status.class);

        // Check
        boolean hasKey = headCommit.getMap().containsKey(fileName);
        if (hasKey) {
            // The fileName was added to headCommit before.
            /*   If content is different, add, else don't. */

            if (!headCommit.compare(fileName, fileHash)) {
                // -> Update Area
                currentArea.updateAdd(fileName, fileHash);
                writeObject(STAGING, currentArea);

                // -> Add Blob
                updateBlob(fileHash, fileContent);

                //System.out.println("the first time add file");
            }
        } else {
            // The fileName didn't be added to headCommit before.
            // -> Update Area
            currentArea.updateAdd(fileName, fileHash);
            writeObject(STAGING, currentArea);

            // -> Add Blob
            updateBlob(fileHash, fileContent);

            //System.out.println("add file");
        }
        //System.out.println("nothing add");
    }

    /** Do commit and clear Area. */
    public static void commit(String message) throws IOException {

        boolean changedTable = false;

        if (!GITLET_DIR.exists()) {
            System.exit(0);
        }
        // Get head commit object and head commit hash.
        Commit currentCommit = getHeadCommit();
        String headHash = readContentsAsString(HEAD);

        // New date
        Instant now = Instant.now();

        // Get staging Area
        StagingArea Area = readObject(STAGING, StagingArea.class);
        Map<String, String> stagedAdd = Area.getStagedAdd();
        Map<String, String> stagedRem = Area.getStagedRem();


        /* Update current commit Object. */
        currentCommit.changeDate(now);
        currentCommit.changeMessage(message);
        currentCommit.changeParentHash1(headHash);
        Map<String, String> currentCommitMap = currentCommit.getMap();

        // Get status object.
        Status status = readObject(STATUS, Status.class);

        // Add to table
        if (!stagedAdd.isEmpty()) {
            for (Map.Entry<String, String> entry : stagedAdd.entrySet()) { // 遍历
                String key = entry.getKey();
                String value = entry.getValue();
                currentCommitMap.put(key, value);

                status.addStagedFile(key);
                writeObject(STATUS, status);
            }
            changedTable = true;
        }

        // Remove from table
        if (!stagedRem.isEmpty()) {
            for (Map.Entry<String, String> entry : stagedRem.entrySet()) { // 遍历
                String key = entry.getKey();
                String value = entry.getValue();
                currentCommitMap.remove(key, value);

                status.removeFile(key);
            }
            changedTable = true;
        }

        if (changedTable) {
            // Save new commit to COMMIT directory.
            updateCommit(currentCommit);

            if (status.getCurrentBranch() == branch) {
                String currentHeadHash = readContentsAsString(HEAD);
                writeContents(SPLIT, currentHeadHash);
            }
        } else {
            message("No changes added to the commit.");
        }
        // Clear Area
        Area.clearStagingArea();
        // Write Area and commit.
        writeObject(STAGING, Area);
    }

    /** Add new Blob to BLOB directory. */
    public static void updateBlob(String fileHash, byte[] fileContent) throws IOException {
        File newBlob = join(BLOB, fileHash);
        if (newBlob.exists()) {
            System.exit(0);
        }
        if (!newBlob.createNewFile()) throw new IOException("fail to create" + newBlob.getAbsolutePath());
        Blob currentBlob = new Blob(fileContent);
        writeObject(newBlob, /*(Serializable)*/ currentBlob);
    }

    /** Add new Commit to Commit directory. */
    public static void updateCommit(Commit currentCommit) throws IOException {
        // Serialized commit to byte.
        byte[] serializedData = serialize(currentCommit);
        // Hashable commit to hash.
        String commitHash = sha1(serializedData);

        /* Store commit hash to HEAD file and COMMIT directory. */
        writeContents(HEAD, commitHash);
        // Create initCommit file which named commitHash under COMMIT directory.
        File initCommit = join(COMMIT, commitHash);
        if (!initCommit.createNewFile()) throw new IOException("fail to create" + initCommit.getAbsolutePath());

        // Write commit into commitHash file.
        writeObject(initCommit, currentCommit);
    }

    /** Remove the file if it is in add staged area; If it's not, check whether it was in head
     *  commit, if it was, add it to remove staged area, remove it from Commit Object when next
     *  commit, and remove the file from the working directory if the user has not already
     *  done so (do not remove it unless it is tracked in the current commit).
     */
    public static void rm(String rmFileName) {
        boolean removed = false;

        File fileName = join(CWD, rmFileName);
        Status status = readObject(STATUS, Status.class);

        /* Check Whether it is in add and remove staged area. */
        // Get Area
        StagingArea Area = readObject(STAGING, StagingArea.class);
        Map<String, String> stagedAdd = Area.getStagedAdd();
        Map<String, String> stagedRem = Area.getStagedRem();

        boolean hasKeyInAdd = stagedAdd.containsKey(rmFileName);
        boolean hadKeyInRem = stagedRem.containsKey(rmFileName);

        // Remove the file if it in Add Staged Area.
        if (hasKeyInAdd) {
            stagedAdd.remove(rmFileName);
            // update status
            status.removeFile(rmFileName);

            removed = true;
        }

        // Add the file to Remove Staged Area if it is in Head Commit, and delect it.
        Commit headCommit = getHeadCommit();
        boolean hasKey = headCommit.getMap().containsKey(rmFileName);
        if (hasKey) {
            if (!hadKeyInRem) {
                stagedRem.put(rmFileName, null);
                // update status
                status.removeFile(rmFileName);
                if (fileName.exists()){
                    boolean success = restrictedDelete(fileName);
                    if (!success) {
                        throw error("Fail to Delete" + fileName);
                    }
                }
            }

            // write Area and status to file.
            writeObject(STAGING, Area);
            writeObject(STATUS, status);
            removed = true;
        }
        if (!removed) {
            throw error("No reason to remove the file.");
        }
    }

    /** Get the head commit object. */
    public static Commit getHeadCommit() {
        // Read head hash form HEAD
        String headHash = readContentsAsString(HEAD);

        // Get head commit object form COMMIT directory.
        File headCommit = join(COMMIT, headHash);
        if (!headCommit.exists()) {
            throw error("Head commit file doesn't exist.");
        }
        Commit currentCommit = readObject(headCommit, Commit.class);
        return currentCommit;
    }

    /** Get any commit object. */
    public static Commit getCommit(String commitHash) {
        File commitFile = join(COMMIT, commitHash);
        if (!commitFile.exists()) {
            throw error("No such commit exists.");
        }

        Commit commit = readObject(commitFile, Commit.class);
        return commit;
    }

    public static void log() {
        String headHash = readContentsAsString(HEAD);
        String parentHash = printCommit(headHash);
        while (parentHash != null) {
            parentHash = printCommit(parentHash);
        }
    }

    public static void globalLog() {
        List<String> allCommits = plainFilenamesIn(COMMIT);
        if (!allCommits.isEmpty()) {
            for (String allCommit : allCommits) {
                printCommit(allCommit);
            }
        }
    }

    public static void find(String message) {
        boolean output = false;
        List<String> allCommits = plainFilenamesIn(COMMIT);
        if (!allCommits.isEmpty()) {
            for (String allCommit : allCommits) {
                File currentCommitFile = join(COMMIT, allCommit);
                Commit currentCommit = readObject(currentCommitFile, Commit.class);
                if (currentCommit.getMessage().equals(message)) {
                    System.out.println(allCommit);
                    output = true;
                }
            }
        }

        if(!output) {
            throw error("Found no commit with that message.");
        }
    }

    public static String printCommit(String fileName) {
        File currentFile = join(COMMIT, fileName);
        if (!currentFile.exists()) {
            throw error("There is no " + fileName + ".");
        }

        Commit currentCommit = readObject(currentFile, Commit.class);
        Instant date = currentCommit.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy Z",
                Locale.US).withZone(ZoneId.systemDefault());
        String formattedTime = formatter.format(date);
        String parentHash1 = currentCommit.getParentHash1();
        String parentHash2 = currentCommit.getParentHash2();
        String message = currentCommit.getMessage();
        if (parentHash2 == null) {
            message("===");
            message("commit " + fileName);
            message("Date: %s", formattedTime);
            message(message);
            System.out.println();
        } else {
            String first = parentHash1.substring(0, 7);
            String second = parentHash2.substring(0, 7);

            message("===");
            message("commit " + fileName);
            message("Merge: " + first + " " + second);
            message("Date: %s", formattedTime);
            message("Merged development into master.");
            System.out.println();
        }

        return parentHash1;
    }

    public static void status() {
        // Get status object.
        Status status = readObject(STATUS, Status.class);
        Set<String> branches = status.getBranches();
        Set<String> stagedFiles = status.getStagedFiles();
        Set<String> removedFiles = status.getRemovedFiles();
        String currentBranch = status.getCurrentBranch();

        message("=== Branches ===");
        for (String item : branches) {
            if (item.equals(currentBranch)) {
                message("*" + item);
            } else {
                message(item);

            }
        }
        System.out.println();

        message("=== Staged Files ===");
        for (String item : stagedFiles) {
            message(item);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String item : removedFiles) {
            message(item);
        }
        System.out.println();

        message("=== Modifications Not Staged For Commit ===");
        System.out.println();

        message("=== Untracked Files ===");
        System.out.println();
    }

    public static void branch(String branch) {
        Status status = readObject(STATUS, Status.class);
        Set<String> branches = status.getBranches();

        if (branches.size() == 2) {
            throw error("Full branches.");
        } else if (branches.contains(branch)) {
            throw error("A branch with that name already exists.");
        }

        // Add new branch to status and update SPLIT.
        branches.add(branch);
        String headHash = readContentsAsString(HEAD);
        writeObject(STATUS, status);
        writeContents(SPLIT, headHash);
    }

    public static void checkout1(String fileName) throws IOException {
        // Get head commit.
        Commit headCommit = getHeadCommit();
        checkout(headCommit, fileName);
    }

    public static void checkout2(String fileName, String fileHash) throws IOException {
        Commit commit = getCommit(fileHash);
        checkout(commit, fileName);
    }

    public static void checkout(Commit commit, String fileName) throws IOException {
        // Check whether the file exists in this commit.
        boolean hasKey = commit.getMap().containsKey(fileName);
        if (!hasKey) {
            throw error("File does not exist in that commit.");
        }

        // Call head version of tht file.
        File checkFile = join(CWD, fileName);

        if (checkFile.exists()) {
            restrictedDelete(checkFile);
        }

        if (checkFile.exists()) {
        }

        if (!checkFile.createNewFile()) throw new IOException("fail to create" + checkFile.getAbsolutePath());

        // Get file blob.
        String fileHash = commit.getMap().get(fileName);
        File file = join(BLOB, fileHash);
        Blob fileBlob = readObject(file, Blob.class);
        byte[] fileContent = fileBlob.getContent();
        writeContents(checkFile, fileContent);
    }


    public static void checkBranch(String branch) throws IOException {
        Status status = readObject(STATUS, Status.class);
        Set<String> branches = status.getBranches();
        Commit headCommit = getHeadCommit();
        List<String> fileList = plainFilenamesIn(CWD);
        Map<String, String> headMap = headCommit.getMap();

        if (!branches.contains(branch)) {
            message("No such branch exists.");
            return;
        } else if (branches.equals(status.getCurrentBranch())) {
            message("No need to checkout the current branch.");
            return;
        } else if (headMap.size() != fileList.size()) {
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }

        String splitHash = readContentsAsString(SPLIT);
        Commit splitCommit = getCommit(splitHash);

        Map<String, String> splitMap = splitCommit.getMap();

        // Change this branch to current branch.
        status.changeCurrentBranch(branch);
        // Change headHash to SPLIT.
        String headHash = readContentsAsString(HEAD);
        writeContents(SPLIT, headHash);
        // Change this commitHash to head commit.
        writeContents(HEAD, splitHash);


        // Clean CWE except "gitlet" and ".gitlet".
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).equals(".gitlet") || fileList.get(i).equals("gitlet")) {
                continue;
            } else {
                // Clean files.
                File currentFile = join(CWD, fileList.get(i));
                restrictedDelete(currentFile);
            }
        }

        /* Make files. */
        for (Map.Entry<String, String> entry : splitMap.entrySet()) {
            // Get file name and file version.
            String key = entry.getKey();
            String value = entry.getValue();
            // Create and write to files.
            File currentFile = join(CWD, key);
            if (!currentFile.createNewFile()) throw new IOException("fail to create" + currentFile.getAbsolutePath());

            // Get files from BLOB, then write content to new files.
            File file = join(BLOB, value);
            Blob fileBlob = readObject(file, Blob.class);
            byte[] fileContent = fileBlob.getContent();
            writeContents(currentFile, fileContent);
        }

        // Write status.
        writeObject(STATUS, status);
        // Clear Area.
        StagingArea Area = readObject(STAGING, StagingArea.class);
        Area.clearStagingArea();
        // Write Area.
        writeObject(STAGING, Area);
    }

    /** Remove branch if there is. */
    public static void remBranch(String branch) {
        // Get status object
        Status status = readObject(STATUS, Status.class);
        Set<String> branches = status.getBranches();

        // Check stations.
        if (!branches.contains(branch)) {
            throw error("A branch with that name does not exist.");
        } else if (status.getCurrentBranch().equals(branch)) {
            throw error("Cannot remove the current branch.");
        }
        // Change current branch to null
        status.changeCurrentBranch(null);
        // Write status
        writeObject(STATUS, status);
    }
}
