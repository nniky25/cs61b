package gitlet;

import Deque.ArrayDeque;

/** Create a commitList to store every commit */
public class CommitList {

    private ArrayDeque<Commit> commitList;

    public CommitList() {
        commitList = new ArrayDeque<Commit> ();
    }

    /** Add commit to CommitList. */
    public void addCommit (Commit commit) {
        commitList.addLast(commit);
    }
}
