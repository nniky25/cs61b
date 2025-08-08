package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;
// TODO: You'll likely use this in this class

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
    /** The date, parentHash1 is master, parentHash2 is main, message, Blob of this Commit. */
    private String date;
    private String parentHash1;
    private String parentHash2;
    private String nextCommitHash;
    private String message;
    private Map<String, String> table = new HashMap<>();

    /** Implement Constructor */
    public Commit(String message, String date, String[] parentHash) {
        this.date = date;
        this.message = message;
        this.parentHash1 = parentHash[0];
        this.parentHash2 = parentHash[1];
    }

    public boolean compare(String fileName, String contentHash) {
        boolean hasKey = getMap().containsKey(fileName);
        if (!hasKey) {
            error("There are no " + fileName + " key in this commit");
            System.exit(0);
        }
        String content = getMap().get(fileName).toString();
        return content.equals(contentHash);
    }

    public void addNextCommitHash(String commitHash) {
        this.nextCommitHash = commitHash;
    }

    public String getNextCommitHash() {
        return this.nextCommitHash;
    }

    public Map<String, String> getMap() {
        return table;
    }

    public String getDate() {
        return date;
    }

    public String getParentHash1() {
        return parentHash1;
    }

    public String getParentHash2() {
        return parentHash2;
    }

    public String getMessage() {
        return message;
    }

    public void changeDate(String currentDate) {
        this.date = currentDate;
    }

    public void changeParentHash1(String currentHash) {
        this.parentHash1 = currentHash;
    }

    public void changeMessage(String currentMessage) {
        this.message = currentMessage;
    }
}
