package org.os;


import java.util.Scanner;

public class driverProgram {
    public static void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(System.getProperty("user.dir") + " > ");
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }
            parseCommand(input);
        }
        scanner.close();
    }

    private static void parseCommand(String input) {
        String[] tokens = input.trim().split("\\s+");
        String command = tokens[0].toLowerCase();
        switch (command) {
            case "cd":
                System.out.println(cmd.cd(tokens));
                break;
            case "mv":
                cmd.mv(tokens);
                break;
            case "pwd":
                System.out.println(cmd.pwd(tokens));
                break;
            case "rmdir":
                System.out.println(cmd.rmdir(tokens));
                break;
            case "rm":
                System.out.println(cmd.rm(tokens));
                break;
//            case ">":
//                cmd.forwardArrow(tokens);
//                break;
            case "cat":
                cmd.cat(tokens);
                break;
//            case "exit":
//                flag = false;
//                break;
//            case "help":
//                return help(tokens);
            default:
                System.out.println("Unknown command: " + command);
        }
    }
}
