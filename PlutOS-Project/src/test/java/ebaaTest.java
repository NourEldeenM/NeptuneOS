import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.os.ebaa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
public class ebaaTest  {
    private ebaa mkdirCommandTest;

    @BeforeEach
    void setUp() {
        mkdirCommandTest = new ebaa();
    }

    @Nested
    class mkdirCommandTest {
        @Test
        public void testCreateNewDirectory(@TempDir Path tempDir) {
            String dirName = tempDir.resolve("newDir").toString();
            mkdirCommandTest.mkdirCommand(dirName);

            assertTrue(Files.exists(Path.of(dirName)), "Directory should have been created.");
        }

        @Test
        public void testExistingDirectory(@TempDir Path tempDir) {
            String dirName = tempDir.resolve("existingDir").toString();
            mkdirCommandTest.mkdirCommand(dirName); // create new directory

            // grb t create already existing directory
            mkdirCommandTest.mkdirCommand(dirName); //already exists
        }
    }

    @Nested
    class touchCommandTest {
        @Test
        public void testCreateNewFile(@TempDir Path tempDir) throws IOException {
            String fileName = tempDir.resolve("newFile.txt").toString();
            mkdirCommandTest.touchCommand(fileName); // create new file

            assertTrue(Files.exists(Path.of(fileName)), "File created.");
        }

        @Test
        public void testUpdateFile(@TempDir Path tempDir) throws IOException {
            String fileName = tempDir.resolve("existingFile.txt").toString();
            mkdirCommandTest.touchCommand(fileName); // create new file
            mkdirCommandTest.touchCommand(fileName); // update last modification time

            long lastModifiedTime = Files.getLastModifiedTime(Path.of(fileName)).toMillis();
            assertTrue(lastModifiedTime > 0, "File has last modified time.");
        }


    }
}
