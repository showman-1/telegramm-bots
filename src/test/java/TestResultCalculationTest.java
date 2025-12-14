import org.example.model.TestResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestResultCalculationTest {

    @Test
    void testPercentageCalculation() {

        TestResult result = new TestResult(10);
        result.setScore(8);

        assertEquals(80.0, result.getPercentage());
    }

    @Test
    void testPerfectScore() {
        TestResult result = new TestResult(15);
        result.setScore(15);

        assertEquals(100.0, result.getPercentage());
    }

    @Test
    void testZeroScore() {
        TestResult result = new TestResult(10);
        result.setScore(0);

        assertEquals(0.0, result.getPercentage());
    }
}