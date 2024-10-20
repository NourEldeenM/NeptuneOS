import org.os.Example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExampleTest {
    @Test
    void threePlusOne() {
        Example example = new Example();
        int result = example.add(3, 1);
        assertEquals(4, result);
    }
}
