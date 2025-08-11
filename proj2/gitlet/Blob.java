package gitlet;

import java.io.Serializable;

/** to Store content. */
public class Blob implements Serializable {
    private byte[] bContent;


    /* Normal blob. */
    public Blob(byte[] byteContent) {
        this.bContent = byteContent;

    }

    public byte[] getContent() {
        return bContent;
    }
}
