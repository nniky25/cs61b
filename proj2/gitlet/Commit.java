package gitlet;

// TODO: any imports you need here
import Deque.ArrayDeque;
import java.util.*;
 // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String date;
    public String parentHash;
    private String message;
    private ArrayDeque<String> fileWithBlob;
    public Map<String, String> Blob;

    /* Implement Constructor */
    // With date
    public Commit(String message, String date, String parentHash) {
        this.date = date;
        this.message = message;
        this.parentHash = parentHash;
        fileWithBlob = new ArrayDeque<String> ();
    }

    public String getdate() {
        return date;
    }

    public String getparentHash() {
        return parentHash;
    }

    public String getmessage() {
        return message;
    }




    /**public static void main(String[] args) {
        new Commit();
    }*/
}
