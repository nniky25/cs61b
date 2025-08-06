package gitlet;

import java.io.Serializable;

/** to Store content. */
public class Blob implements Serializable {
    private byte[] BContent;


    /* Normal blob. */
    public Blob(byte[] byteContent) {
        this.BContent = byteContent;

    }

    public byte[] getContent() {
        return BContent;
    }

    /** public void printLog() {
        if (BBranch == null) {
            System.out.println("===");
            System.out.println("commit " + BHash);
            System.out.println("Date: " + BDate);
            System.out.println(BMessage);
            System.out.println();
        } else {
            System.out.println("===");
            System.out.println("commit " + BHash);
            System.out.println("Merge");
            System.out.println("Date: " + BDate);
            System.out.println(BMessage);
            System.out.println();
        }
    }*/
}
