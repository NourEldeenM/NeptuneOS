package org.os;

import java.io.*;
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
            writer.write(args[1]);
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
}