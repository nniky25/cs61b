package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
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


    /**
     * Full construct (basic) .gitlet/ -- top level for all persistent data.
     *              (basic) - split -- file containing a map object of branches headCommit hash.
     *              (basic) - commit/ -- director containing the Commit hash files.
     *              (basic) - blob/ -- director containing the Blobs of fileHash.
     *              (basic) - head -- file containing the head commit hash.
     *              (basic) - branch -- file containing current branch.
     *              (basic) - status -- fils containing status object.
     *              (basic) - staging -- file containing the Staging Area.
     */

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

        // Set new branches map to SPLIT.
        Map<String, String> branchesMap = new HashMap<>();
        writeObject(SPLIT, (Serializable) branchesMap);

        // Set Status Object to STATUS file.
        Status status = new Status(initBranch);
        status.addBranch(initBranch);
        writeObject(STATUS, status);
    }

    /** Main method:
     * Adds a file to the staging area in preparation for the next commit.
     *
     * <p>
     * The method checks whether the file exists in the working
     * directory. If the file exists, Add the file to stage area
     * if it is not added or changed before.
     * </p>
     *
     * @param fileName the name of the file to add.
     * @throws IOException if an I/O error occurs while reading the file.
     * */
    public static void add(String fileName) throws IOException {
        // Check Whether the file exists.
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
        // Copy file to Staged for addition area if changed.
        updateArea(fileName, fileHash, area, fileContent);
    }

    /** Helper method:
     * update the staging area with a file.
     *
     * <p>
     * Check whether the fileName is tracked in the head commit
     * and if its contents are identical, then update the STAGING
     * fileName and add new blob to BLOB directory.
     * </p>
     *
     * @param fileName the name of the current file.
     * @param fileHash the hash of the current file hash.
     * @param area the area object.
     * @param bytes the byte contents of the current file.
     * @throws IOException if an I/O error occurs while reading the file.
     * */
    private static void updateArea(String fileName, String fileHash, StagingArea area,
                                   byte[] bytes) throws IOException {
        /* */
        // Get headCommit Object and Status Object.
        String headHash = readContentsAsString(HEAD);
        File headFile = join(COMMIT, headHash);

        Status status = readObject(STATUS, Status.class);
        Commit headCommit = readObject(headFile, Commit.class);

        // Check remStage.
        Map<String, String> remMap = area.getStagedRem();
        if (remMap.containsKey(fileName)) {
            if (remMap.get(fileName).equals(fileHash)) {
                remMap.remove(fileName);
                writeObject(STAGING, area);
                status.remRemovedFiles(fileName);
                writeObject(STATUS, status);
                return;
            }
        }
        // Check
        boolean hasKey = headCommit.getMap().containsKey(fileName);
        if (hasKey) {
            // The fileName was added to headCommit before.
            /*   If content is different, add, else don't. */
            if (!headCommit.compare(fileName, fileHash)) {
                // -> Update Area
                area.updateAdd(fileName, fileHash);
                writeObject(STAGING, area);

                // -> Add Blob
                updateBlob(fileHash, bytes);

                //System.out.println("the first time add fileName");
            }
        } else {
            // The fileName didn't be added to headCommit before.
            // -> Update Area
            area.updateAdd(fileName, fileHash);
            writeObject(STAGING, area);

            // -> Add Blob
            updateBlob(fileHash, bytes);

            // -> Add status
            status.addStagedFile(fileName);
            writeObject(STATUS, status);
        }
        //System.out.println("nothing add");
    }

    /** Main method:
     * Do a normal commit and clear area.
     *
     * <p>
     * The method is to commit with a message and clear area. The parent is
     * the last commit, store as hash.
     * </p>
     *
     * @param message the message of this commit.
     * @throws IOException if an I/O error occurs while reading the file.
     * */
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

    /** Main method:
     * Do a merge commit and clear area.
     *
     * <p>The method is to commit with a message and clear area. The parent1 is
     * the commit of the current branch, parent2 is the commit of another branch,
     * both stores as hash.
     * </p>
     *
     * @param message the message of this commit.
     * @param parentHash2 the hash commit of another branch.
     * @throws IOException if an I/O error occurs while reading the file.
     * */
    public static void commit(String message,
                              String parentHash2) throws IOException {

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
        currentCommit.changeParentHash2(parentHash2);
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

    /** Help method:
     * Add new Blob to BLOB directory.
     *
     * @param fileHash the hash of the current file.
     * @param fileContent the content of the current file.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public static void updateBlob(String fileHash, byte[] fileContent) throws IOException {
        File newBlob = join(BLOB, fileHash);

        if (!newBlob.exists()) {
            newBlob.createNewFile();
        }

        Blob currentBlob = new Blob(fileContent);
        writeObject(newBlob, /*(Serializable)*/ currentBlob);
    }

    /** Help method:
     * Add new Commit to Commit directory.
     *
     * @param currentCommit the new commit.
     * @throws IOException if an I/O error occurs while reading the file.
     */
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

    /** Main method:
     * Remove the file from CWD or stageArea.
     *
     * <p>
     * Remove the file if it is in add staged area; If it's not, check whether it was in head
     * commit, if it was, add it to remove staged area, remove it from Commit Object when next
     * commit, and remove the file from the working directory if the user has not already
     * done so (do not remove it unless it is tracked in the current commit).
     * </p>
     *
     * @param rmFileName the name of the file you want to remove.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public static void rm(String rmFileName) throws IOException {
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
            // Read file hash from commit.
            Map<String, String> headMap = headCommit.getMap();
            String fileHash = headMap.get(rmFileName);

            if (!hasKeyInAdd) {
                if (!hadKeyInRem) {
                    stagedRem.put(rmFileName, fileHash);
                    status.removeFile(rmFileName);
                    restrictedDelete(fileName);
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

    /** Help method:
     * Get the head commit object.
     *
     * @return the head commit object.
     */
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

    /** Help method:
     * Get any commit object.
     *
     * @param commitHash the hash of the current commit.
     * @return the head commit object.
     */
    public static Commit getCommit(String commitHash) {
        File commitFile = join(COMMIT, commitHash);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return null;
        }

        Commit commit = readObject(commitFile, Commit.class);
        return commit;
    }

    /** Main method:
     * Prints the commit history starting from the current HEAD commit,
     * following the parent chain backwards until the initial commit.
     *
     * <p>
     * Process:
     * 1. Read the current HEAD commit hash.
     * 2. Print the commit information for the HEAD (via {@code printCommit}).
     * 3. Iteratively follow each commit’s parent hash, printing details
     *    until a commit with no parent is reached (the root commit).
     * </p>
     *
     * This simulates the behavior of {@code git log}, but only for the
     * current branch (no merge history is traversed).
     */
    public static void log() {
        String headHash = readContentsAsString(HEAD);
        String parentHash = printCommit(headHash);
        while (parentHash != null) {
            parentHash = printCommit(parentHash);
        }
    }

    /** Main method:
     * Prints information for all commits stored in the repository,
     * regardless of which branch they belong to.
     *
     * <p>
     * Process:
     * 1. Retrieve all commit hashes stored in the COMMIT directory.
     * 2. For each commit hash, print its commit information using {@code printCommit}.
     *</p>
     *
     * This simulates the behavior of {@code git log --all}.
     */
    public static void globalLog() {
        List<String> allCommits = plainFilenamesIn(COMMIT);
        if (!allCommits.isEmpty()) {
            for (String allCommit : allCommits) {
                printCommit(allCommit);
            }
        }
    }

    /** Main method:
     * Finds and prints the commit hashes of all commits whose messages
     * exactly match the given string.
     *
     * <p>
     * Process:
     * 1. Retrieve all commit hashes stored in the COMMIT directory.
     * 2. For each commit, read its Commit object and compare its message
     *    with the given message.
     * 3. If a commit’s message matches exactly, print its hash.
     * </p>
     *
     * @param message the commit message to search for
     */
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

    /** Help method:
     * Prints the details of a commit.
     *
     * <p>
     * Include:
     *  - Commit hash.
     *  - Merge information (if the commit has two parents).
     *  - Date formatted as "EEE MMM d HH:mm:ss yyyy Z".
     *  - Commit message.
     * </p>
     *
     * If the commit has two parents, prints the first 7 characters of each
     * parent hash to indicate a merge commit.
     *
     * @param fileName the hash of the commit to print
     * @return the hash of the first parent of this commit (or null if none)
     * @throws IllegalArgumentException if the commit file does not exist
     */
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
            message(message);
            System.out.println();
        }

        return parentHash1;
    }

    /** Main method:
     * Displays the current status of the repository.
     *
     * <p>
     * include:
     *  - The current branch.
     *  - All branches in the repository.
     *  - Staged files ready for commit.
     *  - Removed files.
     *  - Modified but unstaged files.
     *  - Untracked files.
     * </p>
     *
     * This method simulates the behavior of `git status` for the Gitlet project.
     */
    public static void status() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

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

    /** Main method:
     * Creates a new branch with the given name pointing to the current HEAD commit.
     *
     * <p>
     * Steps:
     * 1. Checks if a branch with the given name already exists; if so, prints an error.
     * 2. Retrieves the current branch and HEAD commit hash.
     * 3. Adds the new branch to the set of branches in Status.
     * 4. Updates the split point mapping for the new branch in both Status and SPLIT.
     * </p>
     *
     * @param branch the name of the new branch to create.
     */
    public static void addBranch(String branch) {
        Map<String, String> splitMap = readObject(SPLIT, HashMap.class);
        Status status = readObject(STATUS, Status.class);
        Set<String> branches = status.getBranches();

        if (branches.contains(branch)) {
            System.out.println("A branch with that name already exists.");
            return;
        }

        // Get currentBranch.
        String currentBranch = status.getCurrentBranch();
        String point1 = currentBranch + branch;
        String point2 = branch + currentBranch;

        // Add new branch and splitHash to status and update SPLIT.
        branches.add(branch);
        String headHash = readContentsAsString(HEAD);
        status.addSplitHash(point1, headHash);
        status.addSplitHash(point2, headHash);
        writeObject(STATUS, status);
        splitMap.put(branch, headHash);
        writeObject(SPLIT, (Serializable) splitMap);
    }

    /** Help method:
     * Restores a file from the current HEAD commit into the working directory.
     *
     * @param fileName the name of the file to restore.
     * @throws IOException if file operations fail.
     */
    public static void checkout1(String fileName) throws IOException {
        // Get head commit.
        Commit headCommit = getHeadCommit();
        checkout(headCommit, fileName);
    }

    /** Help method:
     * Restores a file from a specified commit into the working directory.
     * Supports abbreviated commit hashes (first 8 characters).
     *
     * @param fileName   the name of the file to restore.
     * @param commitHash the hash of the commit to restore from.
     * @throws IOException if file operations fail.
     */
    public static void checkout2(String fileName, String commitHash) throws IOException {
        Commit commit = null;
        if (commitHash.length() == 8) {
            List<String> allCommits = plainFilenamesIn(COMMIT);
            if (!allCommits.isEmpty()) {
                for (String allCommit : allCommits) {
                    if (commitHash.equals(allCommit.substring(0, 8))) {
                        commit = getCommit(allCommit);
                    }
                }
                if (Objects.equals(commit, null)) {
                    if (commit == null) {
                        System.out.println("No commit with that id exists.");
                        return;
                    }
                }
            }
        } else {
            commit = getCommit(commitHash);
        }

        if (commit == null) {
            return;
        }
        checkout(commit, fileName);
    }

    /** Help method:
     * Checks out a single file from the given commit into the working directory.
     *
     * <p>
     * Steps:
     * 1. Verify that the file exists in the commit; if not, print an error message.
     * 2. Locate (or create) the corresponding file in the current working directory:
     *  - If the file already exists, delete it first.
     *  - Create a new empty file with the same name.
     * 3. Retrieve the file's blob from the blob store using the hash recorded in the commit.
     * 4. Write the blob's contents into the new file, effectively restoring the file
     *    to its version from the given commit.
     * </p>
     *
     * @param commit   the commit containing the desired version of the file.
     * @param fileName the name of the file to check out.
     * @throws IOException if file creation or writing fails.
     */
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

    /** Help method.
     *
     * <p>
     * Checks whether it is safe to checkout the given branch.
     *  - Verifies that the branch exists.
     *  - Prevents checking out the current branch.
     *  - Ensures no untracked files in the working directory would be
     *    overwritten by the branch checkout.
     *  If any of these conditions fail, prints an appropriate error
     *  message and returns without performing the checkout.
     * </p>
     *
     * @param thisBranch the name of the branch to check.
     * @throws IOException if reading from disk fails.
     */
    public static void checkBranch(String thisBranch) throws IOException {
        // Get branches
        Status status = readObject(STATUS, Status.class);
        Set<String> branches = status.getBranches();

        // Get headMap
        Commit headCommit = getHeadCommit();
        Map<String, String> headMap = headCommit.getMap();

        // Get fileList
        List<String> fileList = plainFilenamesIn(CWD);

        /* If a working file is untracked in the current branch and would
         * be overwritten by the checkout, print There is an untracked file
         * in the way; delete it, or add and commit it first.
         */
        if (!branches.contains(thisBranch)) {
            System.out.println("No such branch exists.");
            return;
        } else if (thisBranch.equals(status.getCurrentBranch())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        // Get splitMap and put currentBranch to it.
        Map<String, String> splitMap = readObject(SPLIT, HashMap.class);
        String headHash = readContentsAsString(HEAD);
        splitMap.put(status.getCurrentBranch(), headHash);

        String branchHash = splitMap.get(thisBranch);
        Commit branchCommit = getCommit(branchHash);
        if (branchCommit == null) {
            return;
        }
        Map<String, String> branchMap = branchCommit.getMap();

        for (int i = 0; i < fileList.size(); i++) {
            if (!headMap.containsKey(fileList.get(i)) && branchMap.containsKey(fileList.get(i))) {
                System.out.println("There is an untracked file "
                        + "in the way; delete it, or add and commit it first.");
                return;
            }
        }

        List<String> delectFile = new ArrayList<>();

        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            String key = entry.getKey();
            // Do 1
            if (!branchMap.containsKey(key)) {
                delectFile.add(key);
            }
        }


        // Change this thisBranch to current thisBranch.
        status.changeCurrentBranch(thisBranch);

        // Change headHash to SPLIT.
        writeObject(SPLIT, (Serializable) splitMap);

        // Change this commitHash to head commit.
        writeContents(HEAD, branchHash);

        // Clean CWD and make files.
        cleanCWDandMakeFiles(branchMap, delectFile);

        // Write status.
        writeObject(STATUS, status);
        // Clear area.
        StagingArea area = readObject(STAGING, StagingArea.class);
        area.clearStagingArea();
        // Write area.
        writeObject(STAGING, area);
    }

    /** Help method:
     *
     * <p>
     * Cleans the current working directory (CWD) and recreates files
     * based on the given commit mapping.
     *  - Removes all files in CWD except the internal "gitlet" and ".gitlet" directories.
     *  - For each file entry in the provided map, creates a new file if
     *    necessary and writes the corresponding blob content into it.
     * </p>
     *
     * @param map mapping of file names to blob hashes (from a commit).
     * @param fileList list of file names currently in the working directory.
     * @throws IOException if file creation or writing fails.
     */
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
            if (!currentFile.exists()) {
                currentFile.createNewFile();
            }

            // Get files from BLOB, then write content to new files.
            File file = join(BLOB, value);
            Blob fileBlob = readObject(file, Blob.class);
            byte[] fileContent = fileBlob.getContent();
            writeContents(currentFile, fileContent);
        }
    }

    /** Help method:
     *
     * <p>
     * Remove the Branch if there is.
     * </p>
     *
     * @param thisBranch the name of current branch.
     */
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
        // Change current thisBranch
        status.remBranch(thisBranch);
        // remove branch form SPLIT.
        Map<String, String> splitMap = readObject(SPLIT, HashMap.class);
        splitMap.remove(thisBranch);
        writeObject(SPLIT, (Serializable) splitMap);
        // Write status
        writeObject(STATUS, status);
    }

    /** Main method:
     *
     * <p>
     * Reset the current branch to the given commit:
     *  - Replace working directory files with those in the commit.
     *  - Remove tracked files missing in that commit.
     *  - Move HEAD to the commit.
     *  - Clear the staging area.
     * </p>
     *
     * @param commitHash the hash of current commit.
     * @throws IOException if file operations during merge fail.
     */
    public static void reset(String commitHash) throws IOException {
        // Get current commit map.
        Commit currentCommit = getCommit(commitHash);
        if (currentCommit == null) {
            return;
        }
        Map<String, String> commitMap = currentCommit.getMap();

        // Get fileList and head commit.
        Commit headCommit = getHeadCommit();
        Map<String, String> headMap = headCommit.getMap();

        // Get WD files.
        List<String> fileList = plainFilenamesIn(CWD);

        // Get stagedAdd map.
        StagingArea area = readObject(STAGING, StagingArea.class);
        Map<String, String> stagedAdd = area.getStagedAdd();

        /* 1. Travers to find the files in the current commit map but not in head commit.
         * and create a list to store them, then to find untracked files.
         * 2. Find the files are tracked by head commit but untracked by current commit.
         * and delect them on WD.
         */
        List<String> firstList = new ArrayList<>();

        // full firstList.
        for (Map.Entry<String, String> entry : commitMap.entrySet()) {
            String key = entry.getKey();
            // Do 1
            if (!headMap.containsKey(key)) {
                firstList.add(key);
            }
        }

        // find untracked files and return.
        for (String i : firstList) {
            for (String j : fileList) {
                if (i.equals(j) && !stagedAdd.containsKey(i)) {
                    System.out.println("There is an untracked file "
                            + "in the way; delete it, or add and commit it first.");
                    return;
                }
            }
        }

        // Do 2
        List<String> delectFile = new ArrayList<>();

        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            String key = entry.getKey();
            // Do 1
            if (!commitMap.containsKey(key)) {
                delectFile.add(key);
            }
        }

        // Clean and make files.
        cleanCWDandMakeFiles(commitMap, delectFile);
        // Clear area.
        area.clearStagingArea();
        // Write area.
        writeObject(STAGING, area);
        // Get status
        Status status = readObject(STATUS, Status.class);
        // Clean stage in status.
        status.cleanStage();
        // Write status to STATUS.
        writeObject(STATUS, status);
        // Write head.
        writeContents(HEAD, commitHash);
    }

    /** Main method:
     * Performs a merge of the given branch into the current branch.
     *
     * <p>
     * The method:
     *  - Identifies the split point commit between the current branch head
     *    and the given branch head.
     *  - Compares file versions in the split, current head, and branch head
     *    to determine necessary actions (checkout, stage, remove).
     *  - Detects and marks conflicts if both branches modified a file
     *    differently since the split point.
     *  - Creates a new merge commit that has two parents (current head
     *    and the given branch head).
     * </p>
     *
     * @param thisBranch the name of the branch to merge into the current branch.
     * @throws IOException if file operations during merge fail.
     */
    public static void merge(String thisBranch) throws IOException {
        /* Check before merge. */
        // Get a status object.
        Status status = readObject(STATUS, Status.class);
        Set<String> branches = status.getBranches();
        String currentBranch = status.getCurrentBranch();

        // Get area.
        StagingArea area = readObject(STAGING, StagingArea.class);

        // Check station.
        if (!area.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        } else if (!branches.contains(thisBranch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (currentBranch.equals(thisBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        /* Merge. */
        // Get splitHash, headHash and given branch Hash.
        String point = currentBranch + thisBranch;
        String splitHash = status.getSplitHash(point);
        String headHash = readContentsAsString(HEAD);
        Map<String, String> splitMap = readObject(SPLIT, HashMap.class);
        String branchHash = splitMap.get(thisBranch);
        // Use BFS
        if (Objects.equals(splitHash, null)) {
            splitHash = findSplitPoint(headHash, branchHash);
        }

        // Check station.
        if (splitHash.equals(branchHash)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        } else if (splitHash.equals(headHash)) {
            checkBranch(thisBranch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        // Get all files names and store them to a map.
        Map<String, MergeHelper> helper = files(splitHash, thisBranch);

        /**
         * conflict is an int numebr that presents different stage of merge.
         *  conflict == 0 ---> there is no conflict.
         *  conflict == 1 ---> there is conflict.
         *  conflict == 2 ---> there is an untracked file in the way.
         */
        int conflict = check(helper);
        if (conflict == 2) {
            return;
        }
        if (conflict == 1) {
            System.out.println("Encountered a merge conflict.");
        }
        commit("Merged " + thisBranch + " into " + currentBranch + ".", branchHash);
    }

    /** Help method:
     * Finds the split point (lowest common ancestor commit) of two given
     * commit heads.
     *
     * <p>
     * The method:
     *  - Collects all ancestors of head1.
     *  - Performs a BFS from head2 until it reaches a commit that is also
     *    an ancestor of head1.
     *  - Returns that commit hash as the split point.
     * </p>
     *
     * @param head1 the commit hash of the first branch head.
     * @param head2 the commit hash of the second branch head.
     * @return the commit hash of the split point, or null if none exists.
     */
    public static String findSplitPoint(String head1, String head2) {
        // find all parent 1 ancestors of head1
        Set<String> ancestors1 = new HashSet<>();
        Queue<String> q1 = new LinkedList<>();
        q1.add(head1);
        while (!q1.isEmpty()) {
            String c = q1.poll();
            ancestors1.add(c);
            q1.addAll(getParents(c));
        }

        // BFS
        Queue<String> q2 = new LinkedList<>();
        q2.add(head2);
        while (!q2.isEmpty()) {
            String c = q2.poll();
            if (ancestors1.contains(c)) {
                return c; // find split point
            }
            q2.addAll(getParents(c));
        }
        return null;
    }

    /** Help method:
     *
     * <p>
     * Get parents from commit hash of one branch head, and put them to a list.
     * </p>
     *
     * @param head commit hash of one branch head.
     * @return parents list.
     */
    public static List<String> getParents(String head) {
        List<String> parents = new ArrayList<>();
        Commit commit = getCommit(head);

        if (commit.getParentHash1() != null) {
            parents.add(commit.getParentHash1());
        }
        if (commit.getParentHash2() != null) {
            parents.add(commit.getParentHash2());
        }

        return parents;
    }

    /** Help method:
     *
     * <p>
     * Check the situation of all the files for merge.
     * </p>
     *
     * @param helper a map from file name to a MergeHelper object containing
     *        the three versions (split, head, branch).
     * @return an int number that presents different stage of merge.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public static int check(Map<String, MergeHelper> helper) throws IOException {
        int conflict = 0;
        // Get WD files.
        List<String> fileList = plainFilenamesIn(CWD);

        for (Map.Entry<String, MergeHelper> entry : helper.entrySet()) {
            String key = entry.getKey();
            MergeHelper value = entry.getValue();
            String split = value.getSplitHash();
            String head = value.getHeadHash();
            String given = value.branchHash();

            if (Objects.equals(split, null)) {
                if (Objects.equals(given, null) && !Objects.equals(head, null)) {
                    // Stay
                    continue;
                } else if (!Objects.equals(given, null) && Objects.equals(head, null)) {
                    // Rewrite the file with the version of given and add.
                    if (fileList.contains(key)) {
                        System.out.println("There is an untracked file "
                                + "in the way; delete it, or add and commit it first.");
                        conflict = 2;
                        return conflict;
                    }
                    rewrite(given, key);
                } else {
                    if (Objects.equals(given, head)) {
                        // stay
                        continue;
                    } else {
                        // Conflict when head and given are different.
                        conflict = rewriteForConflict(head, given, key);
                    }
                }
            } else {
                if (Objects.equals(head, null) && !Objects.equals(given, null)) {
                    if (Objects.equals(given, split)) {
                        continue;
                    } else {
                        if (fileList.contains(key)) {
                            System.out.println("There is an untracked file "
                                    + "in the way; delete it, or add and commit it first.");
                            conflict = 2;
                            return conflict;
                        }
                        conflict = rewriteForConflict(head, given, key);
                    }
                } else if (!Objects.equals(head, null) && Objects.equals(given, null)) {
                    if (Objects.equals(head, split)) {
                        rm(key);
                    } else {
                        conflict = rewriteForConflict(head, given, key);
                    }
                } else if (Objects.equals(head, null)) {
                    continue;
                } else {
                    if (Objects.equals(head, split) && !Objects.equals(given, split)) {
                        rewrite(given, key);
                    } else if (Objects.equals(given, split) && !Objects.equals(head, split)) {
                        continue;
                    } else if (Objects.equals(given, split)) {
                        continue;
                    } else {
                        conflict = rewriteForConflict(head, given, key);
                    }
                }
            }
        }
        return conflict;
    }

    /** Help method:
     * rewrite file content for the specific situation of merge.
     *
     * @param blobHash the bolb hash of the current file.
     * @param fileName the file name of the current file.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public static void rewrite(String blobHash, String fileName) throws IOException {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        File blobFile = join(BLOB, blobHash);
        Blob fileBlob = readObject(blobFile, Blob.class);
        byte[] fileContent = fileBlob.getContent();
        writeContents(file, fileContent);
        add(fileName);
    }

    /** Help method:
     * rewrite file content for the conflict situation of merge.
     *
     * @param blobHash1 the bolb hash of the current head file.
     * @param blobHash2 the bolb hash of the current given file.
     * @param fileName the file name of the current file.
     * @return an int number that presents different stage of merge.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public static int rewriteForConflict(String blobHash1,
                                          String blobHash2, String fileName) throws IOException {
        // Put the filet to CWD.
        File file = join(CWD, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        int conflict = 0;
        String fileContent1;
        String fileContent2;

        // Get files from BLOB
        fileContent1 = getConflictContent(blobHash1);
        fileContent2 = getConflictContent(blobHash2);

        // write contents to new files.
        String a = "<<<<<<< HEAD" + "\n";
        String b = "=======" + "\n";
        String c = ">>>>>>>" + "\n";
        String finalContents = a + fileContent1 + b + fileContent2 + c;
        writeContents(file, finalContents);

        conflict = 1;
        add(fileName);
        return conflict;
    }

    /** Help method:
     * Retrieves the content of a blob for resolving merge conflicts.
     *
     * <P>
     * Returns the file content from the blob identified by the given hash.
     * Used specifically during merge conflict handling to retrieve the
     * conflicting version of a file.
     * </P>
     *
     * @param blobHash blob hash of the current file.
     * @return file content.
     */
    public static String getConflictContent(String blobHash) {
        String fileContent;

        if (!Objects.equals(blobHash, null)) {
            File blobFile1 = join(BLOB, blobHash);
            Blob blob1 = readObject(blobFile1, Blob.class);
            byte[] content = blob1.getContent();
            if (Objects.equals(content, null)) {
                fileContent = "";
            } else {
                fileContent =  new String(content, StandardCharsets.UTF_8);
            }
        } else {
            fileContent = "";
        }

        return fileContent;
    }

    /** Help method:
     * Maps each file to its versions in the split point,
     * head, and branch commits for merge conflict detection.
     *
     * <p>
     * Builds a mapping from file names to their corresponding versions
     * (blobs) in the split point, the current head commit, and the given
     * branch commit. Used during merge to detect changes and conflicts.
     * </p>
     *
     * @param splitHash   the commit hash of the split point.
     * @param thisBranch  the name of the branch being merged.
     * @return a map from file name to a MergeHelper object containing
     *         the three versions (split, head, branch).
     */
    public static Map<String, MergeHelper> files(String splitHash, String thisBranch) {
        // Get branchMap.
        Map<String, String> branchesMap = readObject(SPLIT, HashMap.class);
        String branchHash = branchesMap.get(thisBranch);
        Commit branchCommit = getCommit(branchHash);
        Map<String, String> branchMap = branchCommit.getMap();


        // Get headMap
        Commit headCommit = getHeadCommit();
        Map<String, String> headMap = headCommit.getMap();

        // Get splitMap
        Commit splitCommit = getCommit(splitHash);
        Map<String, String> splitMap = splitCommit.getMap();

        // Create a set to store files.
        Set<String> filesName = new HashSet<>();

        filesName = fillSet(filesName, splitMap);
        filesName = fillSet(filesName, headMap);
        filesName = fillSet(filesName, branchMap);

        // Create a map to store fileName and its three blobs.
        Map<String, MergeHelper> helper = new HashMap<>();
        // Store.
        for (String fileName : filesName) {
            String split = splitMap.get(fileName);
            String head = headMap.get(fileName);
            String branch = branchMap.get(fileName);
            MergeHelper node = new MergeHelper(split, head, branch);
            helper.put(fileName, node);
        }
        return helper;
    }

    /** Help method:
     * to store files from map to set.
     *
     * @param filesName a set to store file name.
     * @param map a map containing the three versions (split, head, branch)
     * @return the set.
     */
    public static Set<String> fillSet(Set<String> filesName,
                                      Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            filesName.add(key);
        }
        return filesName;
    }
}
