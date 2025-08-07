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
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
            Utils.error("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.setupPersistence();
                break;
            case "add":
                if (args.length == 1) {
                    Utils.error("Please enter a file.");
                    System.exit(0);
                }
                if (args.length > 2) {
                    Utils.error("Wrong args length.");
                    System.exit(0);
                }
                String adFileName = args[1];
                Repository.add(adFileName);
                break;
            case "commit":
                if (args.length == 1) {
                    Utils.error("Please enter a message.");
                    System.exit(0);
                }
                if (args.length > 2) {
                    Utils.error("Wrong args length.");
                    System.exit(0);
                }
                String message = args[1];
                Repository.commit(message);
                break;
            case "rm":
                if (args.length == 1) {
                    Utils.error("Please enter a file name.");
                    System.exit(0);
                }
                if (args.length > 2) {
                    Utils.error("Wrong args length");
                }
                String rmFileName = args[1];

            // TODO: FILL THE REST IN
        }
    }
}
