package org.os;


import java.util.Scanner;

public class driverProgram {

    private static String whiteText() {
        return "\u001B[37m" + " $ " + "\u001B[0m";
    }

    private static String blueText(String s) {
        return "\u001B[34m" + s + "\u001B[0m";
    }

    public static void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(blueText(System.getProperty("user.dir")) + whiteText());
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }
            parseCommand(input);
        }
        scanner.close();
    }

    public static void parseCommand(String input) {
        String[] tokens = input.trim().split("\\s+");
        String command = tokens[0].toLowerCase();
        if (input.contains(">>")) {
            command = ">>";
        }
        if (input.contains("|")) {
            cmd.handlePipe(input);
            return;
        } else if (input.contains(">")) {
            command = ">";
        }
        switch (command) {
            case "cd":
                cmd.cd(tokens);
                break;
            case "mv":
                cmd.mv(tokens);
                break;
            case "pwd":
                System.out.println(cmd.pwd());
                break;
            case "rmdir":
                System.out.println(cmd.rmdir(tokens));
                break;
            case "rm":
                System.out.println(cmd.rm(tokens));
                break;
            case "ls":
                System.out.print(cmd.ls(tokens));
                break;
            case ">>":
                cmd.appendOutputToFile(tokens);
                break;
            case ">":
                cmd.forwardArrow(tokens);
                break;
            case "cat":
                System.out.println(cmd.cat(tokens));
                break;
            case "help":
                System.out.println(cmd.help());
            case "mkdir":
                cmd.mkdirCommand(tokens);
                break;
            case "touch":
                cmd.touchCommand(tokens);
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }
}

