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
    /** The date, parentHash, message, Blob of this Commit. */
    private  String date;
    public  String parentHash;
    private  String message;
    private Map<String, String> table = new HashMap<>();

    /** Implement Constructor */
    public Commit(String message, String date, String parentHash) {
        this.date = date;
        this.message = message;
        this.parentHash = parentHash;
    }

    public boolean compare(String fileName, String contentHash) {
        boolean hasKey = getMap().containsKey("apple");
        if (!hasKey) {
            error("There are no " + fileName + " key in this commit");
            System.exit(0);
        }
        String content = getMap().get(fileName).toString();
        return content.equals(contentHash);
    }

    public Map getMap() {
        return table;
    }

    public String getDate() {
        return date;
    }

    public String getParentHash() {
        return parentHash;
    }

    public String getMessage() {
        return message;
    }

    public void changeDate(String currentDate) {
        this.date = currentDate;
    }

    public void changeParentHash(String currentHash) {
        this.parentHash = currentHash;
    }

    public void changeMessage(String currentMessage) {
        this.message = currentMessage;
    }
}
