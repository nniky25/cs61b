package gitlet;

import java.io.Serializable;
import java.util.*;

/** Show status of gitlit. */
public class Status implements Serializable {
    private String currentBranch;
    private Set<String> branches = new TreeSet<>();
    private Set<String> stagedFiles = new TreeSet<>();
    private Set<String> removedFiles = new TreeSet<>();

    public Status(String branch) {
        this.currentBranch = branch;
    }
    public void addBranch(String branch) {
        branches.add(branch);
    }

    public void remBranch(String branch) {
        branches.remove(branch);
    }

    /* Add the file to stagedFiles and remove it from removeFiles if be added before. */
    public void addStagedFile(String fileName) {
        stagedFiles.add(fileName);
        if (removedFiles.contains(fileName)) {
            removedFiles.remove(fileName);
        }
    }

    /* Remove the file from stagedFiles to removedFiles. */
    public void removeFile(String fileName) {
        stagedFiles.remove(fileName);
        removedFiles.add(fileName);
    }

    public void remStagedFile(String fileName) {
        stagedFiles.remove(fileName);

    }

    public Set<String> getBranches() {
        return branches;
    }

    public Set<String> getStagedFiles() {
        return stagedFiles;
    }

    public Set<String> getRemovedFiles() {
        return removedFiles;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }

    public void changeCurrentBranch(String branch) {
        this.currentBranch = branch;
    }

    public void cleanStage() {
        stagedFiles.clear();
        removedFiles.clear();
    }
}
