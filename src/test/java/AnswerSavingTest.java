import org.example.model.FriendshipTest;
import org.example.model.TestResult;
import org.example.service.TestManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnswerSavingTest {

    @Test
    void testCreatorAnswersSavedAsCorrect() {

        TestManager manager = new TestManager();
        String testId = manager.createNewTest(123L, "Вася");

        manager.saveAnswer(123L, "Ответ1");
        manager.saveAnswer(123L, "Ответ2");

        FriendshipTest test = manager.getTest(testId);
        assertEquals("Ответ1", test.getQuestions().get(0).getCorrectAnswer());
        assertEquals("Ответ2", test.getQuestions().get(1).getCorrectAnswer());
    }

    @Test
    void testFriendScoreCalculation() {
        TestManager manager = new TestManager();
        String testId = manager.createNewTest(123L, "Создатель");

        FriendshipTest test = manager.getTest(testId);
        test.getQuestions().get(0).setCorrectAnswer("A");
        test.getQuestions().get(1).setCorrectAnswer("B");
        test.getQuestions().get(2).setCorrectAnswer("C");

        Long friendId = 456L;
        manager.startTakingTest(friendId, testId);

        manager.saveAnswer(friendId, "A");
        manager.saveAnswer(friendId, "B");
        manager.saveAnswer(friendId, "X");

        for (int i = 3; i < 15; i++) {
            manager.saveAnswer(friendId, "Любой ответ");
        }

        TestResult result = manager.calculateResults(friendId, testId);
        assertEquals(2, result.getScore());
    }

    @Test
    void testAllCorrect() {
        TestManager manager = new TestManager();
        String testId = manager.createNewTest(123L, "Создатель");

        FriendshipTest test = manager.getTest(testId);
        for (int i = 0; i < 15; i++) {
            test.getQuestions().get(i).setCorrectAnswer("Ответ" + i);
        }

        Long friendId = 456L;
        manager.startTakingTest(friendId, testId);

        for (int i = 0; i < 15; i++) {
            manager.saveAnswer(friendId, "Ответ" + i);
        }

        TestResult result = manager.calculateResults(friendId, testId);
        assertEquals(15, result.getScore());
        assertEquals(100.0, result.getPercentage());
    }

    @Test
    void testAllCorrectSimple() {

        TestManager manager = new TestManager();
        String testId = manager.createNewTest(123L, "Создатель");

        FriendshipTest test = manager.getTest(testId);
        test.getQuestions().get(0).setCorrectAnswer("A");
        test.getQuestions().get(1).setCorrectAnswer("B");
        test.getQuestions().get(2).setCorrectAnswer("C");

        Long friendId = 456L;
        manager.startTakingTest(friendId, testId);

        manager.saveAnswer(friendId, "A");
        manager.saveAnswer(friendId, "B");
        manager.saveAnswer(friendId, "C");

        for (int i = 3; i < 15; i++) {
            manager.saveAnswer(friendId, "Любой");
        }

        TestResult result = manager.calculateResults(friendId, testId);
        assertEquals(3, result.getScore());
        assertEquals(15, result.getTotalQuestions());
    }
}