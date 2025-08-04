package gitlet;

import java.io.File;
import java.io.IOException;
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
    /** The commit directory. */
    public static final File COMMIT = join(GITLET_DIR, "commit");
    /** The head file. */
    public static final File HEAD = join(GITLET_DIR, "head");
    /** The current branch file. */
    public static final File BRANCH = join(GITLET_DIR, "branch");
    /** The staging file. */
    public static final File STAGING = join(GITLET_DIR, "staging");

    Map<String, String> staging = new HashMap<>();


    /* TODO: fill in the rest of this class. */
    /** Full construct (basic) .gitlet/ -- top level for all persistent data.
     *              (other) - split/branchName/ -- director containing the branch split
     *              (basic) - commit/ -- director containing the Commit List.
     *              (basic) - head -- file containing the head commit.
     *              (basic) - branch -- file containing current branch.
     *              (basic) - staging -- file containing the Staging Area.*/
    public static void setupPersistence() throws IOException {
        // init basic construct
        if (!GITLET_DIR.exists()) {
            if (!GITLET_DIR.mkdir()) throw new IOException("fail to mkdir" + GITLET_DIR.getAbsolutePath());
            if (!COMMIT.mkdir()) throw new IOException("fail to mkdir" + COMMIT.getAbsolutePath());
            if (!HEAD.createNewFile()) throw new IOException("fail to create" + HEAD.getAbsolutePath());
            if (!BRANCH.createNewFile()) throw new IOException("fail to create" + BRANCH.getAbsolutePath());
            if (!STAGING.createNewFile()) throw new IOException("fail to create" + STAGING.getAbsolutePath());
        }

        Commit init = new Commit("initial commit", "00:00:00 UTC, Thursday, 1 January 1970", null);
        // Translate commit to hash.
        String commitHash = sha1(init);
        // Store init commit hash to HEAD and COMMIT.
        writeContents(HEAD, commitHash);
        writeContents(COMMIT, commitHash);
    }
}
