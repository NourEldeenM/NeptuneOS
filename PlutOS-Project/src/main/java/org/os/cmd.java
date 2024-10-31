package org.os;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;


// Has all static methods
public class cmd {

    // format text in red
    private static String redText(String s) {
        return "\u001B[31m" + s + "\u001B[0m"; // Change color to red
    }

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
        if (args.length == 1) {
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
     * @param args has content & fileName
     */
    public static void forwardArrow(String[] args) {
        try (FileWriter writer = new FileWriter(args[2])) {
            String line;
            switch (args[0]) {
                case "cd":
                    line = cmd.cd(args);
                    break;
//                case "mv":
//                    line = cmd.mv(args);
//                    break;
                case "pwd":
                    line = cmd.pwd(args);
                    break;
                case "rmdir", "rm":
                    line = "";
                    break;
                // working on moaz's code
                case "ls":
                    line = cmd.ls(args);
                    break;
                case "cat":
                    line = cmd.cat(args);
                    break;
//            case "help":
//                return help(tokens);
                default:
                    line = "Unknown command: " + args[0];
                    break;
            }
            writer.write(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Changes current working directory to directory specified
     *
     * @param args contains directory that we will change to
     * @return
     */
    public static String cd(String[] args) {
        String dirName = args[1];
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
        return dirName;
    }

    /**
     * Moves content of file1 -> file2, & deletes file1
     *
     * @param args has file1 name, file2 name
     */
    public static void mv(String[] args) {
        if (args.length == 2) {
            System.out.print("mv: missing destination file operand after '" + "'");
            System.out.print(args[1]);
            System.out.println("'");
            return;
        }

        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(args[1]));
            writer = new FileWriter(args[2]);
            String line;
            while ((line = reader.readLine()) != null) {
                writer.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();

                // delete original file
                File sourceFile = new File(args[1]);
                if (sourceFile.delete()) {
                    System.out.println("File moved successfully and original file deleted.");
                } else {
                    System.out.println("Error: Could not delete the original file.");
                }
            } catch (IOException e) {
                System.out.println("Error closing file resources: " + e.getMessage());
            }
        }
    }

    /**
     * Prints the current working directory.
     *
     * @param tokens The tokens representing the command and its arguments.
     * @return The current directory as a string.
     */
    public static String pwd(String[] tokens) {
        return System.getProperty("user.dir");
    }

    /**
     * Removes a directory iff it exists and is empty.
     *
     * @param tokens The tokens representing the command and its arguments.
     * @return Result of directory removal as a string.
     */
    public static String rmdir(String[] tokens) {
        String lastError;
        if (tokens.length > 1) {
            String dirName = tokens[1];

            // Check for empty directory name after removing quotes
            if (dirName.equals("\"\"") || dirName.equals("''") || dirName.trim().isEmpty()) {
                lastError = "Error: Directory name not provided.";
                return lastError;
            }

            // Remove surrounding quotes if present
            if ((dirName.startsWith("\"") && dirName.endsWith("\"")) ||
                    (dirName.startsWith("'") && dirName.endsWith("'"))) {
                dirName = dirName.substring(1, dirName.length() - 1);
            }

            File dir = new File(System.getProperty("user.dir"), dirName);

            // Check if the directory exists and is a directory
            if (dir.exists() && dir.isDirectory()) {
                // Check if the directory is empty
                if (Objects.requireNonNull(dir.list()).length == 0) {
                    if (dir.delete()) {
                        return "Directory '" + dirName + "' deleted.";
                    } else {
                        return "Error: Could not delete directory.";
                    }
                } else {
                    return "Error: Directory is not empty.";
                }
            } else {
                return "Error: Directory does not exist.";
            }
        } else {
            lastError = "Error: Directory name not provided.";
            return lastError;
        }
    }

    /**
     * Deletes a file or directory. If the directory is specified, it deletes it along with its subdirectories and files recursively.
     *
     * @param tokens The tokens representing the command and its arguments.
     * @return Result of file deletion as a string.
     */
    public static String rm(String[] tokens) {
        String lastError;
        boolean recursive = false;
        boolean force = false;
        String fileName = "";

        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].equals("-r")) {
                recursive = true;
            } else if (tokens[i].equals("-f") || tokens[i].equals("--force")) {
                force = true;
            } else {
                fileName = tokens[i];
            }
        }

        if ((fileName.startsWith("\"") && fileName.endsWith("\"")) ||
                (fileName.startsWith("'") && fileName.endsWith("'"))) {
            fileName = fileName.substring(1, fileName.length() - 1);
        }

        if (fileName.isEmpty()) {
            lastError = recursive ? "Error: Directory name not provided." : "Error: File name not provided.";
            return lastError;
        }

        File file = new File(fileName);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("user.dir"), fileName);
        }

        if (!file.exists()) {
            lastError = "Error: " + file.getName() + " does not exist.";
            return lastError;
        }

        if (!file.canWrite() && !force) {
            // Prompt user for confirmation
            System.out.print("File '" + file.getName() + "' is unwritable. Do you want to remove it? (y/n): ");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine();
            if (!response.trim().toLowerCase().startsWith("y")) {
                return "File '" + file.getName() + "' skipped.";
            }
        }

        if (file.isDirectory() && recursive) {
            return removeDirectoryRecursively(file);
        } else {
            if (file.delete()) {
                return "File '" + file.getName() + "' deleted.";
            } else {
                lastError = "Error: Could not delete " + file.getName();
                return lastError;
            }
        }
    }

    /**
     * Recursively deletes a directory and all of its contents.
     *
     * @param dir The directory to delete.
     * @return Result of directory deletion as a string.
     */
    private static String removeDirectoryRecursively(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    removeDirectoryRecursively(f);
                } else {
                    rm(new String[]{"rm", f.getAbsolutePath()});
                }
            }
        }
        if (dir.delete()) {
            return "Directory '" + dir.getName() + "' deleted.";
        } else {
            return "Error: Could not delete directory '" + dir.getName() + "'.";
        }
    }

    /**
     * Displays the contents of a directory, including files and subdirectories.
     * <p>
     * This method recursively lists the contents of the specified directory,
     * applying indentation based on the depth of the directory structure.
     * It can filter hidden files based on the 'all' parameter and can
     * display subdirectories recursively if 'recursive' is set to true.
     *
     * @param path   The path to the directory to be displayed.
     * @param indent The indentation level for displaying the contents.
     *               Each level is represented by four spaces.
     * @return A string representation of the directory's contents,
     * formatted with appropriate indentation.
     * @author Moaz Mohamed
     */
    private static String displayDir(String path, int indent, Boolean all, Boolean recursive) {
        if (path.charAt(path.length() - 1) != '/') {
            path += "/";
        }

        File currentDir = new File(path);
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            return "";
        }

        File[] files = currentDir.listFiles();

        if (files == null) {
            return "";
        }


        StringBuilder ans = new StringBuilder();
        String spaces = " ".repeat(indent * 4);
        for (File file : files) {
            if (file.isHidden() && !all) {
                continue;
            }

            ans.append(spaces).append(file.getName());
            if (file.isDirectory()) {
                ans.append('/');
            }

            ans.append("\n");
            if (file.isDirectory() && recursive) {
                String nPath = path + file.getName();


                ans.append(displayDir(nPath, indent + 1, all, recursive));
            }
        }
        return ans.toString();

    }

    /**
     * Lists the contents of a directory based on the provided command tokens.
     * <p>
     * This method processes the 'ls' command, allowing for options to include hidden
     * files and to display the contents recursively. It extracts the path from the
     * command tokens and calls the displayDir method to retrieve the formatted
     * directory listing.
     *
     * @param tokens An array of strings representing the command tokens,
     *               where the first token should be "ls". Additional tokens
     *               may specify options (e.g., "-a" for all files) and the path.
     * @return A string representation of the directory's contents, formatted
     * according to the specified options.
     * @author Moaz Mohamed
     */
    public static String ls(String[] tokens) {
        // Set all booleans to false
        Boolean all = false;
        Boolean recursive = false;

        // Check that line is for ls
        if (!tokens[0].contains("ls")) {
            return "Error: Invalid command: The line must contain 'ls' in first of the line";
        }

        if (tokens.length > 3) {
            return "Error: Your command must be in format of 'command -args attributes'";
        }

        if (tokens.length == 3 && tokens[2].contains("-")) {
            String temp = tokens[1];
            tokens[1] = tokens[2];
            tokens[2] = temp;
        }

        int pathIndex = 1;
        // Set all boolean arguments
        if (tokens.length > 1 && tokens[1].contains("-")) {
            for (char c : tokens[1].toCharArray()) {
                if (c == '-') {
                    continue;
                }
                if (c == 'a') {
                    all = true;
                    continue;
                }
                if (c == 'r') {
                    recursive = true;
                    continue;
                }
                return "Error: This " + c + " argument isn't supported\n";
            }
            pathIndex++;
        }

        // Extract path
        String path = ".";
        if (tokens.length > pathIndex) {
            int start = 0;
            int end = tokens[pathIndex].length();
            if (tokens[pathIndex].contains("\"") || tokens[pathIndex].contains("'")) {
                start++;
                if (tokens[pathIndex].substring(start).contains("\"") || tokens[pathIndex].substring(start).contains("'")) {
                    end--;
                } else {
                    return "Error: Your path is not valid";
                }
            }
            path = tokens[pathIndex].substring(start, end);
        }

        File currentDir = new File(path);
        if (!currentDir.exists()) {
            return "Error: "+ path + " does not exist.\n";
        }
        if (!currentDir.isDirectory()) {
            return "Error: '" + path + " is not a directory.\n";
        }

        // Call display dir function that loops over files in the given path
        String ans = displayDir(path, 0, all, recursive);

        // Return ans
        return ans;
    }

    /**
     * Appends the output of a command to a specified file.
     * <p>
     * This method takes an array of command tokens, where the first token is the
     * command to be written, and the third token is the filename. It verifies that
     * the output redirection is correctly formatted (i.e., command >> file).
     * If the file does not exist, it will be created. The command's output is
     * appended to the specified file.
     *
     * @param tokens An array of strings representing the command tokens.
     *               The first token is the command output, and the third token
     *               specifies the file to which the output should be appended.
     * @author Moaz Mohamed
     */
    public static String appendOutputToFile(String[] tokens) {
        if (tokens.length < 3) {
            return "Error: Output redirection should be in the format: command >> file";
        }

        // Line command >> file
        String command = tokens[0];
        String file = tokens[2];

        File outputFile = new File(file);
        if (!outputFile.exists()) {
            return "Error: " + file + " does not exist.";
        }
        if (!outputFile.canWrite()) {
            return "Error: " + file + " is read-only.";
        }

        StringBuilder line = new StringBuilder();

        tokens = command.trim().split("\\s+");
        String otherCommand = tokens[0].toLowerCase();
        switch (otherCommand) {
            case "cd":
                line.append(cmd.cd(tokens));
                break;
            case "mv":
                // Handle mv command if necessary
                line.append(""); // Add implementation here if needed
                break;
            case "pwd":
                line.append(cmd.pwd(tokens));
                break;
            case "rmdir":
            case "rm":
                // Handle rmdir and rm command if necessary
                line.append(""); // Add implementation here if needed
                break;
            case "ls":
                line.append(cmd.ls(tokens));
                break;
            case "cat":
                line.append(cmd.cat(tokens));
                break;
            case "help":
                line.append("help man\n");
                break;
            default:
                return "Error: Unknown command.";
        }

        if (line.toString().contains("Error")) {
            return line.toString();
        }

        try (FileWriter writer = new FileWriter(file, true)) {
            writer.append(line.toString());
            writer.append(System.lineSeparator());
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }

        return "Output successfully appended to " + file;
    }




}