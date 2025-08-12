package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.*;

public class StagingArea implements Serializable {
    private Map<String, String> stagedAdd = new HashMap<>();
    private Map<String, String> stagedRem = new HashMap<>();

    public Map<String, String> getStagedAdd() {
        return stagedAdd;
    }
    public Map<String, String> getStagedRem() {
        return stagedRem;
    }

    public void updateAdd(String fileName, String fileHash) {
        getStagedAdd().put(fileName, fileHash);
    }

    public void updateRem(String fileName, String fileHash) {
        getStagedRem().put(fileName, fileHash);
    }

    public void clearStagingArea() {
        stagedAdd.clear();
        stagedRem.clear();
    }

    public boolean isEmpty() {
        if (stagedAdd.isEmpty() && stagedRem.isEmpty()) {
            return true;
        }
        return false;
    }
}
