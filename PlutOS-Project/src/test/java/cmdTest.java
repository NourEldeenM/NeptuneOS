import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;
import org.os.cmd;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class cmdTest {

    @Nested
    class catCommandTest {
        @Test
        public void testCatWithFiles(@TempDir Path tempDir) throws IOException {
            Path file1 = Files.createFile(tempDir.resolve("file1.txt"));
            Path file2 = Files.createFile(tempDir.resolve("file2.txt"));
            Files.writeString(file1, "file1\n");
            Files.writeString(file2, "file2\n");

            String result = cmd.cat(new String[]{file1.toString(), file2.toString()});

            String expectedOutput = "file1\nfile2\n"; // verify output
            assertEquals(expectedOutput, result);
        }
    }

    @Nested
    class ForwardArrowTest {

        private final String fileName = "testFile.txt";

        @BeforeEach
        void setUp() throws IOException {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        }

        @AfterEach
        void tearDown() {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        }

        @Test
        void testForwardArrowWritesContentToFile() throws IOException {
            String[] args = {"ignored", "Hello, world!", fileName};

            cmd.forwardArrow(args);

            String content = new String(Files.readAllBytes(Paths.get(fileName)));

            assertEquals("Hello, world!", content, "The content in the file should match the input.");
        }

        @Test
        void testForwardArrowEmptyContent() throws IOException {
            String[] args = {"ignored", "", fileName};
            cmd.forwardArrow(args);
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            assertEquals("", content, "The file should be empty if no content is provided.");
        }

        @Test
        void testForwardArrowNullArgs() {
            assertThrows(NullPointerException.class, () -> cmd.forwardArrow(null),
                    "NullPointerException expected when args is null.");
        }
    }

    @Nested
    class changeDirectoryTest {
        private String initialDir;
        private String homeDir;

        @BeforeEach
        void setUp() {
            initialDir = System.getProperty("user.dir");
            homeDir = System.getProperty("user.home");
        }

        @Test
        void testChangeToParentDirectory() {
            File testSubDir = new File(initialDir, "testSubDir");
            assertTrue(testSubDir.mkdir(), "Failed to create test subdirectory");

            System.setProperty("user.dir", testSubDir.getAbsolutePath());
            assertEquals(testSubDir.getAbsolutePath(), System.getProperty("user.dir"));

            cmd.cd(new String[]{"ignored", ".."});
            assertEquals(initialDir, System.getProperty("user.dir"));

            assertTrue(testSubDir.delete(), "Failed to delete test subdirectory");
        }

        @Test
        void testChangeToHomeDirectory() {
            System.setProperty("user.dir", initialDir);  // Reset to initial directory

            cmd.cd(new String[]{"ignored", "~"});
            assertEquals(homeDir, System.getProperty("user.dir"));
        }

        @Test
        void testChangeToSpecificExistingDirectory() {
            File specificDir = new File(initialDir, "specificDir");
            assertTrue(specificDir.mkdir(), "Failed to create specific directory");

            cmd.cd(new String[]{"ignored", "specificDir"});
            assertEquals(specificDir.getAbsolutePath(), System.getProperty("user.dir"));

            assertTrue(specificDir.delete(), "Failed to delete specific directory");
        }

        @Test
        void testErrorForNonExistingDirectory() {
            System.setProperty("user.dir", initialDir);  // Reset to initial directory

            cmd.cd(new String[]{"ignored", "nonExistentDir"});
            assertEquals(initialDir, System.getProperty("user.dir"));
        }

        @Test
        void testErrorForNoParentDirectory() {
            System.setProperty("user.dir", "/");

            cmd.cd(new String[]{"ignored", ".."});
            assertEquals("/", System.getProperty("user.dir"));
        }
    }

    @Nested
    class fileMoveTest {
        private final String sourceFileName = "sourceTestFile.txt";
        private final String destFileName = "destTestFile.txt";

        @BeforeEach
        void setUp() throws IOException {
            FileWriter writer = new FileWriter(sourceFileName);
            writer.write("This is a test file.");
            writer.close();
        }

        @AfterEach
        void tearDown() {
            File sourceFile = new File(sourceFileName);
            File destFile = new File(destFileName);
            if (sourceFile.exists()) sourceFile.delete();
            if (destFile.exists()) destFile.delete();
        }

        @Test
        void testMissingDestinationOperand() {
            String[] args = {"ignored", sourceFileName};
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            cmd.mv(args);
            assertTrue(outputStream.toString().contains("mv: missing destination file operand"),
                    "Should warn about missing destination operand.");
        }

        @Test
        void testFileMovedSuccessfully() throws IOException {
            String[] args = {"ignored", sourceFileName, destFileName};
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            cmd.mv(args);
            File destFile = new File(destFileName);
            assertTrue(destFile.exists(), "Destination file should exist after moving.");
            String content = new String(Files.readAllBytes(destFile.toPath()));
            assertEquals("This is a test file." + System.lineSeparator(), content, "File content should match.");
            File sourceFile = new File(sourceFileName);
            assertFalse(sourceFile.exists(), "Source file should be deleted after moving.");
            assertTrue(outputStream.toString().contains("File moved successfully and original file deleted."),
                    "Output should confirm successful file move and deletion.");
        }
    }

    @Nested
    class pwdCommandTests {
        @Test
        public void testPwd() {
            String[] tokens = {"pwd"};
            String result = cmd.pwd(tokens);
            String expectedDir = System.getProperty("user.dir");
            assertEquals(expectedDir, result, "PWD command failed");
        }
    }

    @Nested
    class rmdirCommandTests {

        @BeforeEach
        public void setUpRmdir() {
            new File("testEmptyDir").mkdir();
            File nonEmptyDir = new File("testNonEmptyDir");
            nonEmptyDir.mkdir();
            try {
                new File(nonEmptyDir, "testFile.txt").createNewFile();
            } catch (Exception ignored) {
            }
        }

        @Test
        public void testRmdirEmptyDirectory() {
            String result = cmd.rmdir(new String[]{"rmdir", "testEmptyDir"});
            assertEquals("Directory 'testEmptyDir' deleted.", result, "RMDIR command failed on empty directory");
        }

        @Test
        public void testRmdirNonEmptyDirectory() {
            String result = cmd.rmdir(new String[]{"rmdir", "testNonEmptyDir"});
            assertEquals("Error: Directory is not empty.", result, "RMDIR command did not handle non-empty directory correctly");
        }

        @Test
        public void testRmdirNonExistentDirectory() {
            String result = cmd.rmdir(new String[]{"rmdir", "nonExistentDir"});
            assertEquals("Error: Directory does not exist.", result, "RMDIR command did not handle non-existent directory correctly");
        }
    }

    @Nested
    class RmCommandTests {

        @BeforeEach
        public void setUpRm() {
            try {
                new File("testFile.txt").createNewFile();
                File dir = new File("testDirRecursive");
                dir.mkdir();
                new File(dir, "testFile.txt").createNewFile();
            } catch (Exception ignored) {
            }
        }

        @Test
        public void testRmFile() {
            String result = cmd.rm(new String[]{"rm", "testFile.txt"});
            assertEquals("File 'testFile.txt' deleted.", result, "RM command failed on file deletion");
        }

        @Test
        public void testRmNonExistentFile() {
            String result = cmd.rm(new String[]{"rm", "nonExistentFile.txt"});
            assertEquals("Error: nonExistentFile.txt does not exist.", result, "RM command did not handle non-existent file correctly");
        }

        @Test
        public void testRmDirectoryRecursive() {
            String result = cmd.rm(new String[]{"rm", "-r", "testDirRecursive"});
            assertEquals("Directory 'testDirRecursive' deleted.", result, "RM command failed on recursive directory deletion");
        }
    }

//    ls command tests

    @Nested
    class LsCommandTests {

        @BeforeEach
        public void setUpLs() {
            try {
                // Create some test files and directories
                new File("testFile1.txt").createNewFile();
                new File("testFile2.txt").createNewFile();
                File testDir = new File("testDir");
                testDir.mkdir();
                new File(testDir, "testFileInDir.txt").createNewFile();
            } catch (Exception ignored) {
            }
        }

        @AfterEach
        public void tearDown() {
            // Clean up the created files and directories after each test
            new File("testFile1.txt").delete();
            new File("testFile2.txt").delete();
            File dir = new File("testDir");
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            dir.delete();
        }

        @Test
        public void testLsNoArgs() {
            String result = cmd.ls(new String[]{"ls"});
            assertTrue(result.contains("testFile1.txt"), "LS command should list 'testFile1.txt'");
            assertTrue(result.contains("testFile2.txt"), "LS command should list 'testFile2.txt'");
            assertTrue(result.contains("testDir"), "LS command should list 'testDir'");
        }

        @Test
        public void testLsWithDirectory() {
            String result = cmd.ls(new String[]{"ls", "testDir"});
            assertTrue(result.contains("testFileInDir.txt"), "LS command should list 'testFileInDir.txt' in 'testDir'");
        }

        @Test
        public void testLsNonExistentDirectory() {
            String result = cmd.ls(new String[]{"ls", "nonExistentDir"});
            assertEquals("Error: nonExistentDir does not exist.\n", result, "LS command did not handle non-existent directory correctly");
        }

        @Test
        public void testLsWithInvalidArgs() {
            String result = cmd.ls(new String[]{"ls", "-invalidArg"});
            assertEquals("Error: This i argument isn't supported\n", result, "LS command should handle an unsupported argument correctly");
        }
        @Test
        public void testLsWithAllFlag() {
            // Create a hidden file for testing
            try {
                new File(".hiddenFile.txt").createNewFile();
            } catch (Exception ignored) {
            }

            String result = cmd.ls(new String[]{"ls", "-a"});
            // Check if the output includes the hidden file and other visible files
            assertTrue(result.contains("testFile1.txt"), "LS command should list 'testFile1.txt'");
            assertTrue(result.contains("testFile2.txt"), "LS command should list 'testFile2.txt'");
            assertTrue(result.contains("testDir"), "LS command should list 'testDir'");
            assertTrue(result.contains(".hiddenFile.txt"), "LS command should list '.hiddenFile.txt' when using -a");

            // Clean up
            new File(".hiddenFile.txt").delete();
        }
        @Test
        public void testLsWithRecursiveFlag() {
            // Create a directory with nested directories and files for testing
            File nestedDir = new File("testDir/nestedDir");
            nestedDir.mkdirs();
            try {
                new File(nestedDir, "nestedFile.txt").createNewFile();
            } catch (Exception ignored) {
            }

            String result = cmd.ls(new String[]{"ls", "-r", "testDir"});
            // Check if the output includes the nested file
            assertTrue(result.contains("nestedDir"), "LS command should list 'nestedDir' in 'testDir'");
            assertTrue(result.contains("nestedFile.txt"), "LS command should list 'nestedFile.txt' in 'nestedDir'");

            // Clean up
            new File(nestedDir, "nestedFile.txt").delete();
            nestedDir.delete();
        }
    }



}
