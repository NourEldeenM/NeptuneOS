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
}
