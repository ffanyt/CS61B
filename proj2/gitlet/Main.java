package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        //String firstArg = "global_log";
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validArgs(args, 1);
                Repository.Init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validArgs(args, 2);
                String file = args[1];
//                String file = "test.txt";
                Repository.add(file);
                break;
            case "commit":
                validArgs(args, 2);
                String ms = args[1];
                //String ms = "test";
//                System.out.println("指行了commit指令");
//                System.out.println("message是：" + ms);
                Repository.commit(ms);
                break;
            case "rm":
                validArgs(args, 2);
                Repository.checkinilization();
                String rmfile = args[1];
//                String rmfile = "test.txt";
                Repository.rm(rmfile);
                break;
            case "log":
                validArgs(args, 1);
                Repository.checkinilization();
                Repository.log();
                break;
            case "global_log":
                validArgs(args, 1);
                Repository.checkinilization();
                Repository.global_log();
                break;
            case "find":
                validArgs(args, 2);
                Repository.checkinilization();
                String message = args[1];
                //String message = "ms";
                Repository.find(message);
                break;
            case "status":
                validArgs(args, 1);
                Repository.checkinilization();
                Repository.status();
                break;
            case "checkout":
                int len = args.length;
                switch (len) {
                    case 2:
                        Repository.checkout(args[1], 0);
                        break;
                    case 3:
                        if (args[1] != "--") {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        Repository.checkout(args[2], 1);
                        break;
                    case 4:
                        if (args[2] != "--") {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        Repository.checkout(args[1], args[3]);
                        break;
                    default:
                        Repository.printError("Incorrect operands.");
                }
                break;
            case "branch":
                validArgs(args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                validArgs(args, 2);
                Repository.rm_branch(args[1]);
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
