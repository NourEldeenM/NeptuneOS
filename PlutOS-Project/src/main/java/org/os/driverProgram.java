package org.os;


import java.util.Scanner;

public class driverProgram {

//        Style text with white color
    private static String whiteText(String s) {
        return "\u001B[37m" + s + "\u001B[0m";
    }

//        style text with blue text
    private static String blueText(String s) {
        return "\u001B[34m" + s + "\u001B[0m";
    }
//     style to color text red
    private static String redText(String s) {
        return "\u001B[31m" + s + "\u001B[0m";
    }



    public static void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(blueText(System.getProperty("user.dir")) + whiteText(" $ "));
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }

            parseCommand(input);
        }
        scanner.close();
    }

    public static String parseCommand(String input) {
        String[] tokens = input.trim().split("\\s+");
        String command = tokens[0].toLowerCase();
        if (input.contains(">>")) {
            command = ">>";
        } else if (input.contains(">")) {
            command = ">";
        }
        String output=null;

        switch (command) {
            case "cd":
                output = cmd.cd(tokens);
                break;
            case "mv":
                cmd.mv(tokens);
                break;
            case "pwd":
                output = cmd.pwd(tokens);
                break;
            case "rmdir":
                output = cmd.rmdir(tokens);
                break;
            case "rm":
                output = cmd.rm(tokens);
                break;
            case "ls":
                output = cmd.ls(tokens);
                break;
            case ">>":
                cmd.appendOutputToFile(tokens);
                break;
            case ">":
                cmd.forwardArrow(tokens);
                break;
            case "cat":
                output = cmd.cat(tokens);
                break;
            // case "help":
            //     return help(tokens);
            default:
                output = "Error: Unknown command" + command;
        }

        if (output != null && !output.isEmpty()) {
            if(output.contains("Error")){
                System.out.print(redText(output));
            }else{
                System.out.print(output);
            }
        }

        return "";
    }
}
