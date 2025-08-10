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
            System.out.println(errorMessage);
            System.exit(0);
        }
        if (args.length > 2) {
            System.out.println("Wrong args length.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Please enter a command.");
                System.exit(0);
            }

            String firstArg = args[0];
            switch(firstArg) {
                case "init":
                    if (args.length != 1) {
                        System.out.println("Wrong args length.");
                        System.exit(0);
                    }
                    Repository.setupPersistence();
                    break;
                case "add":
                    correct("Please enter a file.", args);
                    Repository.add(args[1]);
                    break;
                case "commit":
                    correct("Please enter a message.", args);
                    Repository.commit(args[1]);
                    break;
                case "rm":
                    correct("Please enter a file name.", args);
                    Repository.rm(args[1]);
                    break;
                case "log":
                    if (args.length != 1) {
                        System.out.println("Wrong args length.");
                        System.exit(0);
                    }
                    Repository.log();
                    break;
                case "global-log":
                    if (args.length != 1) {
                        System.out.println("Wrong args length.");
                        System.exit(0);
                    }
                    Repository.globalLog();
                    break;
                case "find":
                    correct("Please enter a find message.", args);
                    Repository.find(args[1]);
                    break;
                case "status":
                    if (args.length != 1) {
                        System.out.println("Wrong args length.");
                        System.exit(0);
                    }
                    Repository.status();
                    break;
                case "branch":
                    correct("Please enter a new branch.", args);
                    Repository.branch(args[1]);
                    break;
                case "checkout":
                    if (args.length == 1) {
                        System.out.println("please enter check message.");
                        System.exit(0);
                    }
                    if (args.length == 2) {
                        Repository.checkBranch(args[1]);
                    }
                    else if (args.length == 3) {
                        if (!args[1].equals("--")) {
                            System.out.println("second should be '--'.");
                            System.exit(0);
                        }
                        Repository.checkout1(args[2]);
                    }
                    else if (args.length == 4) {
                        if (!args[2].equals("--")) {
                            System.out.println("second should be '--'.");
                            System.exit(0);
                        }
                        Repository.checkout2(args[1], args[3]);
                    }
                    break;
                case "rm-branch":
                    if (args.length != 2) {
                        System.out.println("Wrong args length.");
                        System.exit(0);
                    }
                    Repository.remBranch(args[1]);
                    break;
                default:
                    System.out.println("No command with that name exists.");
                    System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("An IO error occurred: " + e.getMessage());
            System.exit(1);
        }
    }
}