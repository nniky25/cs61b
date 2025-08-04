package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import static gitlet.Utils.*;
import java.util.*;

public class StagingArea implements Serializable {
    private Map<String, String> stagedAdd = new HashMap<>();
    private Map<String, String> stagedRem = new HashMap<>();

    private Map<String, String> getStagedAdd() { return stagedAdd; }
    private Map<String, String> getStagedRem() { return stagedRem; }

    public void updateAdd(String fileName, String fileHash) {
        getStagedAdd().put(fileName, fileHash);
    }

    public void updateRem(String fileName, String fileHash) {
        getStagedRem().put(fileName, fileHash);
    }
}
