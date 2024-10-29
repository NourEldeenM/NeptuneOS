import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.os.Example;
import org.junit.jupiter.api.Test;
import org.os.nour;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class nourTest {

    @Nested
    class catCommandTest {
        @Test
        public void testCatWithFiles(@TempDir Path tempDir) throws IOException {
            // create 2 temp files and store in data in them
            Path file1 = Files.createFile(tempDir.resolve("file1.txt"));
            Path file2 = Files.createFile(tempDir.resolve("file2.txt"));
            Files.writeString(file1, "file1\n");
            Files.writeString(file2, "file2\n");

            // calculate result to be asserted
            String result = nour.cat(new String[]{file1.toString(), file2.toString()});

            String expectedOutput = "file1\nfile2\n"; // verify output
            assertEquals(expectedOutput, result);
        }
    }

    @Nested
    class forwardArrowTest {
        @Test
        // test 1: write content to existing file
        public void testForwardArrowWritesContentToFile(@TempDir Path tempDir) throws IOException {
            Path tempFile = tempDir.resolve("testFile.txt"); // create temp file for testing
            String content = "test content.";
            nour.forwardArrow(content, tempFile.toString()); // write content to that file
            String fileContent = Files.readString(tempFile); // get content from file and check it is equal what you wrote
            assertEquals(content, fileContent, "Content in the file should match the input content.");
        }

        @Test
        public void testForwardArrowCreatesFileIfNotExists(@TempDir Path tempDir) {
            Path tempFile = tempDir.resolve("newFile.txt");
            String content = "Sample content";

            assertTrue(Files.notExists(tempFile));
            nour.forwardArrow(content, tempFile.toString());

            assertTrue(Files.exists(tempFile), "File should be created by forwardArrow method");
            try {
                String fileContent = Files.readString(tempFile);
                assertEquals(content, fileContent, "Content in created file should match the input content");
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }

    @Nested
    class changeDirectoryTest {
        private String initialDir;
        private String homeDir;

        @BeforeEach
        void setUp() {
            // Store the initial directory and home directory for restoring purposes.
            initialDir = System.getProperty("user.dir");
            homeDir = System.getProperty("user.home");
        }

        @Test
        void testChangeToParentDirectory() {
            // Change to a subdirectory, then use `cd("..")` to go back to the parent directory.
            File testSubDir = new File(initialDir, "testSubDir");
            assertTrue(testSubDir.mkdir(), "Failed to create test subdirectory");

            System.setProperty("user.dir", testSubDir.getAbsolutePath());
            assertEquals(testSubDir.getAbsolutePath(), System.getProperty("user.dir"));

            nour.cd("..");
            assertEquals(initialDir, System.getProperty("user.dir"));

            // Cleanup
            assertTrue(testSubDir.delete(), "Failed to delete test subdirectory");
        }

        @Test
        void testChangeToHomeDirectory() {
            // Use `cd("~")` and verify that the directory changes to the home directory.
            System.setProperty("user.dir", initialDir);  // Reset to initial directory

            nour.cd("~");
            assertEquals(homeDir, System.getProperty("user.dir"));
        }

        @Test
        void testChangeToSpecificExistingDirectory() {
            // Create a specific directory, change into it, and verify.
            File specificDir = new File(initialDir, "specificDir");
            assertTrue(specificDir.mkdir(), "Failed to create specific directory");

            nour.cd("specificDir");
            assertEquals(specificDir.getAbsolutePath(), System.getProperty("user.dir"));

            // Cleanup
            assertTrue(specificDir.delete(), "Failed to delete specific directory");
        }

        @Test
        void testErrorForNonExistingDirectory() {
            // Attempt to change to a non-existing directory and check for error handling.
            System.setProperty("user.dir", initialDir);  // Reset to initial directory

            nour.cd("nonExistentDir");
            assertEquals(initialDir, System.getProperty("user.dir"));
        }

        @Test
        void testErrorForNoParentDirectory() {
            // Attempt to go to the parent directory from the root directory, which has no parent.
            System.setProperty("user.dir", "/");  // Setting to root (on UNIX-like systems)

            nour.cd("..");
            assertEquals("/", System.getProperty("user.dir"));
        }

    }
}
