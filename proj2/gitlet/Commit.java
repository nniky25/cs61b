package gitlet;

import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;
import java.time.Instant;


/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Garry
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** The date, parentHash1 is master, parentHash2 is main, message, Blob of this Commit. */
    private Instant date;
    private String parentHash1;
    private String parentHash2;
    //private String nextCommitHash;
    private String message;
    private Map<String, String> table = new HashMap<>();

    /** Implement Constructor */
    public Commit(String message, Instant date, String parentHash) {
        this.date = date;
        this.message = message;
        this.parentHash1 = parentHash;
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

    public Map<String, String> getMap() {
        return table;
    }

    public Instant getDate() {
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

    public void changeDate(Instant currentDate) {
        this.date = currentDate;
    }

    public void changeParentHash1(String currentHash) {
        this.parentHash1 = currentHash;
    }

    public void changeParentHash2(String currentHash) {
        this.parentHash2 = currentHash;
    }

    public void changeMessage(String currentMessage) {
        this.message = currentMessage;
    }
}
