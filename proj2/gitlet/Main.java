package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Repository.printError("Please enter a command.");
        }
        String firstArg = args[0];
//        String firstArg = "reset";
        switch(firstArg) {
            case "init":
                validArgs(args, 1);
                Repository.init();
                break;
            case "add":
                Repository.checkinilization();
                validArgs(args, 2);
                String file = args[1];
//                String file = "f.txt";
                Repository.add(file);
                break;
            case "commit":
                Repository.checkinilization();
                validArgs(args, 2);
                String ms = args[1];
                //String ms = "test";
                Repository.commit(ms);
                break;
            case "rm":
                Repository.checkinilization();
                validArgs(args, 2);
                String rmfile = args[1];
                //String rmfile = "123.txt";
                Repository.rm(rmfile);
                break;
            case "log":
                Repository.checkinilization();
                validArgs(args, 1);
                Repository.log();
                break;
            case "global-log":
                Repository.checkinilization();
                validArgs(args, 1);
                Repository.globalLog();
                break;
            case "find":
                Repository.checkinilization();
                validArgs(args, 2);
                String message = args[1];
                //String message = "initial commit";
                Repository.find(message);
                break;
            case "status":
                Repository.checkinilization();
                validArgs(args, 1);
                Repository.status();
                break;
            case "checkout":
                Repository.checkinilization();
                int len = args.length;
//                int len = 2;
                switch (len) {
                    case 2:
                        Repository.checkout(args[1], 0);
//                        Repository.checkout("other", 0);
                        break;
                    case 3:
                        if (!args[1].equals("--")) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        Repository.checkout(args[2], 1);
//                        Repository.checkout(arg[2], 1);
                        break;
                    case 4:
                        if (!args[2].equals("--")) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        Repository.checkout(args[1], args[3]);
//                        Repository.checkout("2615af", "wug.txt");
                        break;
                    default:
                        Repository.printError("Incorrect operands.");
                }
                break;
            case "branch":
                Repository.checkinilization();
                validArgs(args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.checkinilization();
                validArgs(args, 2);
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                Repository.checkinilization();
                validArgs(args,2);
                Repository.reset(args[1]);
//                Repository.reset("d2c275");
                break;
            case "merge":
                Repository.checkinilization();
                validArgs(args, 2);
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
    private static void validArgs(String[] cm, int len) {
        if (cm.length != len) {
            Repository.printError("Incorrect operands.");
        }
    }
}
