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
}
