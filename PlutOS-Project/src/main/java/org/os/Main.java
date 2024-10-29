//import java.util.Scanner;
//
//public static void main(String[] args) {
//    start();
//}
//
///**
// * Starts the CLI application and processes user input in a loop until exiting.
// */
//public static void start() {
//    Scanner scanner = new Scanner(System.in);
//
//    while (running) {
//        System.out.print(System.getProperty("user.dir") + " > ");
//        String input = scanner.nextLine();
//        System.out.println(parseCommand(input));
//    }
//    scanner.close();
//}
//
///**
// * Parses and executes the given command.
// *
// * @param input The input command string entered by the user.
// * @return The result of the command execution as a string.
// */
//public static String parseCommand(String input) {
//    String[] tokens = input.trim().split("\\s+");
//    String command = tokens[0].toLowerCase();
//    switch (command) {
//        case "pwd":
//            return pwd(tokens);
//        case "rmdir":
//            return rmdir(tokens);
//        case "rm":
//            return rm(tokens);
//        case "cd":
//            return cd(tokens);
//        case "exit":
//            return exit(tokens);
//        case "help":
//            return help(tokens);
//        default:
//            lastError = "Error: Unknown command.";
//            return lastError;
//    }
//}