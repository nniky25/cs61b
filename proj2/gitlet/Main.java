package gitlet;

import java.io.IOException;
import java.util.*;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    public static void correct(String errorMessage, String[] args) {
        if (args.length == 1) {
            throw Utils.error(errorMessage);
        }
        if (args.length > 2) {
            throw Utils.error("Wrong args length.");
        }
    }


    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
            throw Utils.error("Please enter a command.");
        }
        String firstArg = args[0];

        switch(firstArg) {
            case "init":
                if (args.length > 2) {
                    throw Utils.error("Wrong args length.");
                }
                Repository.setupPersistence();
                break;
            case "add":
                correct("Please enter a file.", args);
                String adFileName = args[1];
                Repository.add(adFileName);
                break;
            case "commit":
                correct("Please enter a message.", args);
                String message = args[1];
                Repository.commit(message);
                break;
            case "rm":
                correct("Please enter a file name.", args);
                String rmFileName = args[1];
                Repository.rm(rmFileName);
                break;
            case "log":
                if (args.length > 2) {
                    throw Utils.error("Wrong args length.");
                }
                Repository.log();
                break;
            case "global-log":
                if (args.length > 2) {
                    throw Utils.error("Wrong args length.");
                }
                Repository.globalLog();
                break;
            case "find":
                correct("Please enter a find message.", args);
                String findMessage = args[1];
                Repository.find(findMessage);
                break;
            case "status":
                if (args.length > 2) {
                    throw Utils.error("Wrong args length.");
                }
                Repository.status();
                break;
            case "branch":
                correct("Please enter a new branch.", args);
                String branch = args[1];
                Repository.branch(branch);
                break;
            case "checkout":
                String one = args[1];

                //String three = args[3];
                if (args.length == 1) {
                    throw Utils.error("please enter check message.");
                }
                if (args.length == 2) {
                    String checkBranch = args[1];
                    Repository.checkBranch(checkBranch);
                }
                if (args.length == 3) {
                    if (one.equals("--")) {
                        String checkFile = args[2];
                        Repository.checkout1(checkFile);
                    } else {
                        throw Utils.error("second should be '--'. ");
                    }
                }
                if (args.length == 4) {
                    String symbol = args[2];
                    if (symbol.equals("--")) {
                        Repository.checkout2(one, args[3]);
                    } else {
                        throw Utils.error("second should be '--'. ");
                    }
                }
                break;
            case "rm-branch":
                if (args.length > 3) {
                    throw Utils.error("Wrong args length.");
                }
                Repository.remBranch(args[2]);
                break;
        }
    }
}
