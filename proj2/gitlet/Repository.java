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

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Garry
 */
public class Repository implements Serializable {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    private static String initBranch = "master";
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
            if (!GITLET_DIR.mkdir()) {
                throw new IOException("fail to mkdir" + GITLET_DIR.getAbsolutePath());
            }
            if (!SPLIT.createNewFile()) {
                throw new IOException("fail to create" + SPLIT.getAbsolutePath());
            }
            if (!COMMIT.mkdir()) {
                throw new IOException("fail to mkdir" + COMMIT.getAbsolutePath());
            }
            if (!BLOB.mkdir()) {
                throw new IOException("fail to mkdir" + BLOB.getAbsolutePath());
            }
            if (!HEAD.createNewFile()) {
                throw new IOException("fail to create" + HEAD.getAbsolutePath());
            }
            if (!BRANCH.createNewFile()) {
                throw new IOException("fail to create" + BRANCH.getAbsolutePath());
            }
            if (!STATUS.createNewFile()) {
                throw new IOException("fail to create" + STATUS.getAbsolutePath());
            }
            if (!STAGING.createNewFile()) {
                throw new IOException("fail to create" + STAGING.getAbsolutePath());
            }
        } else {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            return;
        }
        Instant now = Instant.now();
        Commit init = new Commit("initial commit",  now, null);

        // Save new commit to COMMIT directory.
        updateCommit(init);

        // Set Area Object to STAGING file.
        StagingArea area = new StagingArea();
        writeObject(STAGING, area);

        // Set Status Object to STATUS file.
        Status status = new Status(initBranch);
        status.addBranch(initBranch);
        writeObject(STATUS, status);
    }

    /** Do add. */
    public static void add(String fileName) throws IOException {
        //Check Whether the file exists.
        File currentFile = join(CWD, fileName);
        if (!currentFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        // Read File content as byte.
        byte[] fileContent = readContents(currentFile);

        // serialized fileContent and get hash.
        String fileHash = sha1(fileContent);

        // Update
        StagingArea area = readObject(STAGING, StagingArea.class);
        /* Copy file to Staged for addition area if changed. */
        updateArea(fileName, fileHash, area, fileContent);
    }

    /** Check, then update STAGING file and add new blob to BLOB directory. */
    private static void updateArea(String file, String hash, StagingArea area,
                                   byte[] bytes) throws IOException {
        /* Check the file if was added to headCommit and if the same content if added. */
        // Get headCommit Object and Status Object.
        String headHash = readContentsAsString(HEAD);
        File headFile = join(COMMIT, headHash);

        Status status = readObject(STATUS, Status.class);

        Commit headCommit = readObject(headFile, Commit.class);

        // Check remStage.
        Map<String, String> remMap = area.getStagedRem();
        if (remMap.containsKey(file)) {
            if (remMap.get(file).equals(hash)) {
                remMap.remove(file);
                writeObject(STAGING, area);
                status.remRemovedFiles(file);
                writeObject(STATUS, status);
                return;
            }
        }
        // Check
        boolean hasKey = headCommit.getMap().containsKey(file);
        if (hasKey) {
            // The file was added to headCommit before.
            /*   If content is different, add, else don't. */

            if (!headCommit.compare(file, hash)) {
                // -> Update Area
                area.updateAdd(file, hash);
                writeObject(STAGING, area);

                // -> Add Blob
                updateBlob(hash, bytes);

                //System.out.println("the first time add file");
            }
        } else {
            // The file didn't be added to headCommit before.
            // -> Update Area
            area.updateAdd(file, hash);
            writeObject(STAGING, area);

            // -> Add Blob
            updateBlob(hash, bytes);

            // -> Add status
            status.addStagedFile(file);
            writeObject(STATUS, status);

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

        // Get status
        Status status = readObject(STATUS, Status.class);

        // New date
        Instant now = Instant.now();

        // Get staging area
        StagingArea area = readObject(STAGING, StagingArea.class);
        Map<String, String> stagedAdd = area.getStagedAdd();
        Map<String, String> stagedRem = area.getStagedRem();

        /* Update current commit Object. */
        currentCommit.changeDate(now);
        currentCommit.changeMessage(message);
        currentCommit.changeParentHash1(headHash);
        Map<String, String> currentCommitMap = currentCommit.getMap();

        // Add to table
        if (!stagedAdd.isEmpty()) {
            for (Map.Entry<String, String> entry : stagedAdd.entrySet()) { // 遍历
                String key = entry.getKey();
                String value = entry.getValue();
                currentCommitMap.put(key, value);

            }
            changedTable = true;
        }

        // Remove from table
        if (!stagedRem.isEmpty()) {
            for (Map.Entry<String, String> entry : stagedRem.entrySet()) { // 遍历
                String key = entry.getKey();
                String value = entry.getValue();
                currentCommitMap.remove(key, value);

            }
            changedTable = true;
        }

        if (changedTable) {
            // Save new commit to COMMIT directory.
            updateCommit(currentCommit);
            // Clean stage in status.
            status.cleanStage();
            // Write status to STATUS.
            writeObject(STATUS, status);
        } else {
            message("No changes added to the commit.");
        }
        // Clear area
        area.clearStagingArea();
        // Write area and commit.
        writeObject(STAGING, area);
    }

    /** Add new Blob to BLOB directory. */
    public static void updateBlob(String fileHash, byte[] fileContent) throws IOException {
        File newBlob = join(BLOB, fileHash);
        if (newBlob.exists()) {
            System.exit(0);
        }
        if (!newBlob.createNewFile()) {
            throw new IOException("fail to create" + newBlob.getAbsolutePath());
        }
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
        if (!initCommit.createNewFile()) {
            throw new IOException("fail to create" + initCommit.getAbsolutePath());
        }

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
        // Get area
        StagingArea area = readObject(STAGING, StagingArea.class);
        Map<String, String> stagedAdd = area.getStagedAdd();
        Map<String, String> stagedRem = area.getStagedRem();

        boolean hasKeyInAdd = stagedAdd.containsKey(rmFileName);
        boolean hadKeyInRem = stagedRem.containsKey(rmFileName);

        // Remove the file if it in Add Staged area.
        if (hasKeyInAdd) {
            stagedAdd.remove(rmFileName);
            // update status
            status.remStagedFile(rmFileName);

            removed = true;
        }

        // Add the file to Remove Staged area if it is in Head Commit, and delect it.
        Commit headCommit = getHeadCommit();
        boolean hasKey = headCommit.getMap().containsKey(rmFileName);

        if (hasKey) {
            if (!hasKeyInAdd) {
                if (!hadKeyInRem) {
                    stagedRem.put(rmFileName, null);
                    status.removeFile(rmFileName);
                }
            } else {
                stagedAdd.remove(rmFileName);
                // update status
                status.remStagedFile(rmFileName);
            }
            removed = true;
        }

        // write area and status to file.
        writeObject(STAGING, area);
        writeObject(STATUS, status);

        if (!removed) {
            System.out.println("No reason to remove the file.");
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
            System.out.println("No commit with that id exists.");
            return null;
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

        if (!output) {
            message("Found no commit with that message.");
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

    public static void addBranch(String branch) {
        Status status = readObject(STATUS, Status.class);
        Set<String> branches = status.getBranches();

        if (branches.contains(branch)) {
            System.out.println("A branch with that name already exists.");
            return;
        } else if (branches.size() == 2) {
            System.out.println("Full branches.");
            return;
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

    public static void checkout2(String fileName, String commitHash) throws IOException {
        Commit commit = getCommit(commitHash);
        if (commit == null) {
            return;
        }
        checkout(commit, fileName);
    }

    public static void checkout(Commit commit, String fileName) throws IOException {
        // Check whether the file exists in this commit.
        boolean hasKey = commit.getMap().containsKey(fileName);
        if (!hasKey) {
            message("File does not exist in that commit.");
            return;
        }

        // Call head version of tht file.
        File checkFile = join(CWD, fileName);

        if (checkFile.exists()) {
            restrictedDelete(checkFile);
        }

        if (!checkFile.createNewFile()) {
            throw new IOException("fail to create" + checkFile.getAbsolutePath());
        }

        // Get file blob.
        String fileHash = commit.getMap().get(fileName);
        File file = join(BLOB, fileHash);
        Blob fileBlob = readObject(file, Blob.class);
        byte[] fileContent = fileBlob.getContent();
        writeContents(checkFile, fileContent);
    }

    public static void checkBranch(String thisBranch) throws IOException {
        Status status = readObject(STATUS, Status.class);
        Set<String> branches = status.getBranches();
        Commit headCommit = getHeadCommit();
        List<String> fileList = plainFilenamesIn(CWD);
        Map<String, String> headMap = headCommit.getMap();

        if (!branches.contains(thisBranch)) {
            System.out.println("No such branch exists.");
            return;
        } else if (thisBranch.equals(status.getCurrentBranch())) {
            System.out.println("No need to checkout the current branch.");
            return;
        } else if (headMap.size() != fileList.size()) {
            System.out.println("There is an untracked file "
                    + "in the way; delete it, or add and commit it first.");
            return;
        }

        String splitHash = readContentsAsString(SPLIT);
        Commit splitCommit = getCommit(splitHash);
        if (splitCommit == null) {
            return;
        }

        Map<String, String> splitMap = splitCommit.getMap();

        // Change this thisBranch to current thisBranch.
        status.changeCurrentBranch(thisBranch);
        // Change headHash to SPLIT.
        String headHash = readContentsAsString(HEAD);
        writeContents(SPLIT, headHash);
        // Change this commitHash to head commit.
        writeContents(HEAD, splitHash);

        // Clean CWD and make files.
        cleanCWDandMakeFiles(splitMap, fileList);

        // Write status.
        writeObject(STATUS, status);
        // Clear area.
        StagingArea area = readObject(STAGING, StagingArea.class);
        area.clearStagingArea();
        // Write area.
        writeObject(STAGING, area);
    }

    public static void cleanCWDandMakeFiles(Map<String, String> map,
                                            List<String> fileList) throws IOException {
        // Clean CWE except "gitlet" and ".gitlet".
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).equals(".gitlet") || fileList.get(i).equals("gitlet")) {
                return;
            } else {
                // Clean files.
                File currentFile = join(CWD, fileList.get(i));
                restrictedDelete(currentFile);
            }
        }

        /* Make files. */
        for (Map.Entry<String, String> entry : map.entrySet()) {
            // Get file name and file version.
            String key = entry.getKey();
            String value = entry.getValue();
            // Create and write to files.
            File currentFile = join(CWD, key);
            if (!currentFile.createNewFile()) {
                throw new IOException("fail to create" + currentFile.getAbsolutePath());
            }

            // Get files from BLOB, then write content to new files.
            File file = join(BLOB, value);
            Blob fileBlob = readObject(file, Blob.class);
            byte[] fileContent = fileBlob.getContent();
            writeContents(currentFile, fileContent);
        }
    }

    /** Remove thisBranch if there is. */
    public static void remBranch(String thisBranch) {
        // Get status object
        Status status = readObject(STATUS, Status.class);
        Set<String> branches = status.getBranches();

        // Check stations.
        if (!branches.contains(thisBranch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (status.getCurrentBranch().equals(thisBranch)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        // Change current thisBranch to null
        status.remBranch(thisBranch);
        // Write status
        writeObject(STATUS, status);
    }

    public static void reset(String commitHash) throws IOException {
        Commit currentCommit = getCommit(commitHash);
        if (currentCommit == null) {
            return;
        }
        List<String> fileList = plainFilenamesIn(CWD);

        Map<String, String> map = currentCommit.getMap();

        // Clean and make files.
        cleanCWDandMakeFiles(map, fileList);
        // Clear area.
        StagingArea area = readObject(STAGING, StagingArea.class);
        area.clearStagingArea();
        // Write area.
        writeObject(STAGING, area);
        // Write head.
        writeContents(HEAD, commitHash);
    }
}
