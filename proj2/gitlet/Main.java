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
            Utils.error(errorMessage);
            System.exit(0);
        }
        if (args.length > 2) {
            Utils.error("Wrong args length.");
            System.exit(0);
        }
    }


    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
            Utils.error("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];

        switch(firstArg) {
            case "init":
                if (args.length > 2) {
                    Utils.error("Wrong args length.");
                    System.exit(0);
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
                    Utils.error("Wrong args length.");
                    System.exit(0);
                }
                Repository.log();
                break;
            case "global-log":
                if (args.length > 2) {
                    Utils.error("Wrong args length.");
                    System.exit(0);
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
                    Utils.error("Wrong args length.");
                    System.exit(0);
                }
                Repository.status();
                break;
        }
    }
}
