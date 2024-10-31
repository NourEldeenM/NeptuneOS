package org.os;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

// Has all static methods
public class cmd {
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
            String line = switch (args[0]) {
                case "cd" -> cmd.cd(args);
                case "pwd" -> cmd.pwd();
                case "rmdir", "rm" -> "";
                case "ls" -> cmd.ls(args);
                case "cat" -> cmd.cat(args);
                case "help" -> cmd.help();
                default -> "Unknown command: " + args[0];
            };
            writer.write(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Changes current working directory to directory specified
     *
     * @param args contains directory that we will change to
     * @return new directory name
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
     * @return The current directory as a string.
     */
    public static String pwd() {
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
            System.out.print("File '" + file.getName() + "' is not writable. Do you want to remove it? (y/n): ");
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

//        set all booleans to false
        boolean all = false, recursive = false;

//        check that line is for ls
        if (!tokens[0].contains("ls")) {
            throw new IllegalArgumentException("Invalid command: The line must contain 'ls'");
        }

//        set all boolean arguments
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
                throw new IllegalArgumentException("This " + c + "argument didn't supported\n");
            }
        }

        //extract path
        String path = getPath(tokens);


//      call display dir function that loop over files in given path
        return displayDir(path, 0, all, recursive);
    }

    private static String getPath(String[] tokens) {
        String path = ".";
        if (tokens.length >= 3) {
            int start = 0;
            int end = tokens[2].length();
            if (tokens[2].contains("\"") || tokens[2].contains("'")) {
                start++;
                if (tokens[2].substring(start).contains("\"") || tokens[2].substring(start).contains("'")) {
                    end--;
                } else {
                    throw new IllegalArgumentException("your path is not valid");
                }
            }
            path = tokens[2].substring(start, end);
        }
        return path;
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
    public static void appendOutputToFile(String[] tokens) {
        if (tokens.length < 3) {
            throw new IllegalArgumentException("Output redirection should be in the format: command >> file");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tokens[2], true))) {
            writer.write(tokens[0]);
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Creates a new directory with the specified name.
     *
     * @param tokens The tokens representing the command and its arguments.
     * @return A success or error message indicating the result of the operation.
     */
    public static String mkdirCommand(String[] tokens) {
        // Check if the directory name is provided (and not empty)
        if (tokens.length < 2 || tokens[1].isEmpty()) {
            return "Error: Invalid directory name.";
        }

        String dirName = tokens[1]; // Second token: directory name
        String path = tokens.length > 2 ? tokens[2] : System.getProperty("user.dir");

        File directory = new File(path, dirName);

        System.out.println("Path provided: " + path);
        System.out.println("Directory absolute path: " + directory.getAbsolutePath());

        if (directory.exists()) {
            return "Error: Directory already exists.";
        }
        if (directory.mkdir()) {
            return "Directory '" + dirName + "' created at " + directory.getAbsolutePath();
        } else {
            return "Error: Could not create directory.";
        }
    }

    /**
     * Creates a new file or updates the last modified time of an existing file.
     *
     * @param tokens The tokens representing the command and its arguments.
     * @return A success or error message indicating the result of the operation.
     */
    public static String touchCommand(String[] tokens) {
        if (tokens.length < 2) {
            return "Error: File name not provided.";
        }

        String fileName = tokens[1];

        // Remove surrounding quotes if present
        if ((fileName.startsWith("\"") && fileName.endsWith("\"")) ||
                (fileName.startsWith("'") && fileName.endsWith("'"))) {
            fileName = fileName.substring(1, fileName.length() - 1);
        }

        File file = new File(System.getProperty("user.dir"), fileName);
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    return "File '" + fileName + "' created successfully.";
                }
            } else {
                if (file.setLastModified(System.currentTimeMillis())) {
                    return "File '" + fileName + "' updated successfully.";
                } else {
                    return "Error: Could not update the file '" + fileName + "'.";
                }
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }

        return "Error: Could not create or update the file '" + fileName + "'.";
    }

   
    /**
     * Executes a series of commands separated by pipes ("|").
     * Each command is processed in order, and the output of each
     * command is displayed to the user.
     *
     * @param input A string of commands separated by pipes.
     */
    public static void handlePipe(String input) {
        String[] commands = pipe(input);
        String lastOutput = "";

        for (int i = 0; i < commands.length; i++) {
            String command = commands[i].trim();
            String[] tokens = command.split("\\s+");
            String commandName = tokens[0].toLowerCase();

            switch (commandName) {
                case "ls":
                    lastOutput = cmd.ls(tokens);
                    break;
                case "pwd":
                    lastOutput = cmd.pwd();
                    break;
                case "cat":
                    lastOutput = cmd.cat(tokens);
                    break;
                case "grep":
                    lastOutput = cmd.grep(tokens, lastOutput);
                    break;
                case "mkdir":
                    System.out.println(cmd.mkdirCommand(tokens));
                    lastOutput = "";
                    break;
                case "touch":
                    System.out.println(cmd.touchCommand(tokens));
                    lastOutput = "";
                    break;
                default:
                    System.out.println("Unknown command in pipe: " + commandName);
            }
        }
        // Print the last output if it's not empty
        if (!lastOutput.isEmpty()) {
            System.out.println(lastOutput);
        }
    }
    /**
     * Filters lines from the input that contain the specified pattern.
     * This method is used to display only matching lines from previous command outputs.
     * @param tokens An array of ("grep") and pattern we're searching for.
     * @param input  A string containing lines to be searched.
     * @return A string with lines that match the pattern, or an empty one.
     */
    public static String grep(String[] tokens, String input) {
        String pattern = tokens.length > 1 ? tokens[1] : "";
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        for (String line : lines) {
            if (line.contains(pattern)) {
                output.append(line).append("\n");
            }
        }
        return output.toString().trim();
    }

    /**
     * Splits an input string into commands using the pipe character ("|").
     *
     * @param input A string of commands separated by pipes.
     * @return An array of commands as strings.
     */
    public static String[] pipe(String input) {
        String[] commands = input.split("\\|");
        List<String> commandList = new ArrayList<>();
        for (String command : commands) {
            commandList.add(command.trim());
        }
        return commandList.toArray(new String[0]);
    }

    /**
     * Prints a description of all available methods with their usage and required parameters.
     *
     * @return A string representing the list of all commands with their descriptions.
     */
    public static String help() {

        return """
                Available Commands:
                
                1. cat [file1 file2 ...]
                   Concatenates the contents of the specified files and prints the result. If no file names are provided, it takes user input until '^C' is entered.
                
                2. forwardArrow [command] > [filename]
                   Executes a command and saves its output to the specified file.
                   Supported commands: cd, pwd, rmdir, rm, ls, cat, help.
                
                3. cd [directory]
                   Changes the current working directory to the specified directory. Use '..' to move to the parent directory and '~' to go to the home directory.
                
                4. mv [sourceFile] [destinationFile]
                   Moves the content of the source file to the destination file and deletes the source file.
                
                5. pwd
                   Prints the current working directory.
                
                6. rmdir [directory]
                   Removes an empty directory with the specified name.
                
                7. rm [options] [file/directory]
                   Deletes the specified file or directory. Use '-r' for recursive deletion of directories and '-f' to force delete.
                
                8. ls [options] [path]
                   Lists the contents of the specified directory.
                   Options: '-a' to include hidden files, '-r' for recursive listing.
                
                9. help
                   Displays this help information for all commands.
                """;
    }

}
