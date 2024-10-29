package org.os;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;


public class nour {
    /**
     * Concatenates the contents of the files and prints the result. If we use cat with no
     * arguments, it will take input
     * from user and then print it on
     * screen.
     *
     * @param args The arguments added after the cat command represent file names.
     * @return String of concatenated file contents, or text to be printed
     */
    public static String cat(String[] args) {
        if (args.length == 0) {
            String userInput = "";
            while (!Objects.equals(userInput, "^C")) {
                Scanner scanner = new Scanner(System.in);
                userInput = scanner.nextLine();
                System.out.println(userInput);
            }
            return "";
        }
        StringBuilder output = new StringBuilder();
        for (String fileName : args) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append('\n');
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return output.toString();
    }

    /**
     * Saves the text added in content variable inside file passed.
     *
     * @param content  the text that will be added inside fileName
     * @param fileName the file that the text will be added into
     */
    public static void forwardArrow(String content, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

     public static void cd(String dirName) {
         if (dirName.equals("..")) {
             File currentDir = new File(System.getProperty("user.dir"));
             if (currentDir.getParent() != null) {
                 System.setProperty("user.dir", currentDir.getParent());
                 System.out.println("Directory changed to: " + currentDir.getParent());
             } else {
                 System.out.println("Error: No parent directory.");
             }
         } else if (dirName.equals("~")) {
             String homeDir = System.getProperty("user.home");
             System.setProperty("user.dir", homeDir);
             System.out.println("Directory changed to: " + homeDir);
         } else {
             File dir = new File(System.getProperty("user.dir"), dirName);
             if (dir.exists() && dir.isDirectory()) {
                 System.setProperty("user.dir", dir.getAbsolutePath());
                 System.out.println("Directory changed to: " + dir.getAbsolutePath());
             } else {
                 System.out.println("Error: Directory does not exist.");
             }
         }
     }

}
