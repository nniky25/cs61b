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

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Please enter a command.");
                return;
            }

            String firstArg = args[0];
            switch(firstArg) {
                case "init":
                    if (args.length != 1) {
                        throw Utils.error("Wrong args length.");
                    }
                    Repository.setupPersistence();
                    break;
                case "add":
                    correct("Please enter a file.", args);
                    Repository.add(args[1]);
                    break;
                case "commit":
                    correct("Please enter a message.", args);
                    String message = args[1];
                    // when the message is "".
                    if (message.trim().isEmpty()) {
                        System.out.println("Please enter a commit message.");
                        break;
                    }
                    Repository.commit(args[1]);
                    break;
                case "rm":
                    correct("Please enter a file name.", args);
                    Repository.rm(args[1]);
                    break;
                case "log":
                    if (args.length != 1) {
                        throw Utils.error("Wrong args length.");
                    }
                    Repository.log();
                    break;
                case "global-log":
                    if (args.length != 1) {
                        throw Utils.error("Wrong args length.");
                    }
                    Repository.globalLog();
                    break;
                case "find":
                    correct("Please enter a find message.", args);
                    Repository.find(args[1]);
                    break;
                case "status":
                    if (args.length != 1) {
                        throw Utils.error("Wrong args length.");
                    }
                    Repository.status();
                    break;
                case "branch":
                    correct("Please enter a new branch.", args);
                    Repository.addBranch(args[1]);
                    break;
                case "checkout":
                    if (args.length == 1) {
                        throw Utils.error("please enter check message.");
                    }
                    if (args.length == 2) {
                        Repository.checkBranch(args[1]);
                    }
                    else if (args.length == 3) {
                        if (!args[1].equals("--")) {
                            System.out.println("Incorrect operands.");
                            break;
                        }
                        Repository.checkout1(args[2]);
                    }
                    else if (args.length == 4) {
                        if (!args[2].equals("--")) {
                            System.out.println("Incorrect operands.");
                            break;
                        }
                        Repository.checkout2(args[3], args[1]);
                    }
                    break;
                case "rm-branch":
                    if (args.length != 2) {
                        throw Utils.error("Wrong args length.");
                    }
                    Repository.remBranch(args[1]);
                    break;
                case "reset":
                    if (args.length != 2) {
                        throw Utils.error("Wrong args length.");
                    }
                    Repository.reset(args[1]);
                    break;
                default:
                    System.out.println("No command with that name exists.");
            }
        } catch (IOException e) {
            System.out.println("An IO error occurred: " + e.getMessage());
            System.exit(1); // exit with error
        }
    }
}