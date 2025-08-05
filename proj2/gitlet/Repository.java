package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
    private String branch = "master";
    private String otherBranch;
    private static StagingArea Area = new StagingArea();

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory. */
    public static final File COMMIT = join(GITLET_DIR, "commit");
    /** The blob directory. */
    public static final File BLOB = join(GITLET_DIR, "blob");
    /** The head file. */
    public static final File HEAD = join(GITLET_DIR, "head");
    /** The current branch file. */
    public static final File BRANCH = join(GITLET_DIR, "branch");
    /** The staging file. */
    public static final File STAGING = join(GITLET_DIR, "staging");


    /* TODO: fill in the rest of this class. */
    /** Full construct (basic) .gitlet/ -- top level for all persistent data.
     *              (other) - split/branchName/ -- director containing the branch split
     *              (basic) - commit/ -- director containing the Commit List.
     *              (basic) - blob/ -- director containing the Blobs of fileHash.
     *              (basic) - head -- file containing the head commit.
     *              (basic) - branch -- file containing current branch.
     *              (other) - staging -- file containing the Staging Area.*/
    /** Set StagingArea. */

    /** init basic construct. */
    public static void setupPersistence() throws IOException {
        if (!GITLET_DIR.exists()) {
            if (!GITLET_DIR.mkdir()) throw new IOException("fail to mkdir" + GITLET_DIR.getAbsolutePath());
            if (!COMMIT.mkdir()) throw new IOException("fail to mkdir" + COMMIT.getAbsolutePath());
            if (!BLOB.mkdir()) throw new IOException("fail to mkdir" + BLOB.getAbsolutePath());
            if (!HEAD.createNewFile()) throw new IOException("fail to create" + HEAD.getAbsolutePath());
            if (!BRANCH.createNewFile()) throw new IOException("fail to create" + BRANCH.getAbsolutePath());
        }
        
        Commit init = new Commit("initial commit", "00:00:00 UTC, Thursday, 1 January 1970", null);
        // Serialized commit to byte.
        byte[] serializedData = serialize(init);
        // Hashable commit to hash.
        String commitHash = sha1(serializedData);
        // Store init commit hash to HEAD and COMMIT.
        writeContents(HEAD, commitHash);
        writeContents(COMMIT, commitHash);
        // Set current branch
        writeContents(BRANCH, "master");
    }

    public static void add(String fileName) throws IOException {
        //Check Whether the file exists.
        File currentFile = join(CWD, fileName);
        if (!currentFile.exists()) {
            Utils.error("File does not exist.");
            System.exit(0);
        }
        // Read File content as byte.
        byte[] fileContent = readContents(currentFile);


        // serialized fileContent and get hash.
        byte[] fileByte = serialize(fileContent);
        String fileHash = sha1(fileContent);
        // Update
        if (!STAGING.exists()) {
            if (!STAGING.createNewFile()) throw new IOException("fail to create" + STAGING.getAbsolutePath());
            /* Copy file to Staged for addition area if changed. */
            updateArea(fileName, fileHash, Area, fileByte);
        } else {
            StagingArea currentArea = readObject(STAGING, StagingArea.class);
            updateArea(fileName, fileHash, currentArea, fileByte);
        }
    }

    /** Check, then update STAGING file and add new blob to BLOB directory. */
    private static void updateArea(String fileName, String fileHash, StagingArea currentArea, byte[] fileByte) throws IOException {
        /* Check the fileName if was added to headCommit and if the same content if added. */
        Commit headCommit = readObject(HEAD, Commit.class);
        boolean hasKey = headCommit.getMap().containsKey(fileName);

        if (hasKey) {
            // The fileName was added to headCommit before.
            /*   If content is different, add, else don't. */
            if (!headCommit.compare(fileName, fileHash)) {
                // -> Update Area
                currentArea.updateAdd(fileName, fileHash);
                writeObject(STAGING, currentArea);

                // -> Add Blob
                updateBlob(fileHash, fileByte);

                //System.out.println("the first time add file");
            }

        } else {
            // The fileName didn't be added to headCommit before.
            // -> Update Area
            currentArea.updateAdd(fileName, fileHash);
            writeObject(STAGING, currentArea);

            // -> Add Blob
            updateBlob(fileHash, fileByte);

            //System.out.println("add file");
        }
        //System.out.println("nothing add");
    }

    public void commit(String message) {
        if (!GITLET_DIR.exists()) {
            System.exit(0);
        }
        String headHash = readContentsAsString(HEAD);
        String currentDate = new Date().toString();
        Commit currentCommit = new Commit(message, currentDate, headHash);
    }

    /** Add new Blob to BLOB directory. */
    public static void updateBlob(String fileHash, byte[] fileByte) throws IOException {
        File newBlob = join(BLOB, fileHash);
        if (!newBlob.createNewFile()) throw new IOException("fail to create" + newBlob.getAbsolutePath());
        Blob currentBlob = new Blob(fileByte);
        writeObject(newBlob, (Serializable) currentBlob);
    }
}
