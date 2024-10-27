package org.os;
import java.util.Objects;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class nour {
    /**
     * Concatenates the contents of the files and prints the result. If we use cat with no
     * arguments, it will take input
     * from user and then print it on
     * screen.
     * @param args The arguments added after the cat command represent file names.
     * @return String of concatenated file contents, or text to be printed
     */
     public static String cat(String[] args){
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
}
