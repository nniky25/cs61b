package gitlet;

public class MergeHelper {
    private String splitHash;
    private String headHash;
    private String branchHash;

    public MergeHelper(String split, String head, String branch) {
        this.splitHash = split;
        this.headHash = head;
        this.branchHash = branch;
    }

    public String getSplitHash() {
        return splitHash;
    }

    public String getHeadHash() {
        return headHash;
    }
    public String branchHash() {
        return branchHash;
    }
}
