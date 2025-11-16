package org.example.service;

import org.example.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TestManager {

    private Map<String, FriendshipTest> tests = new ConcurrentHashMap<>();
    private Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    private static final List<Question> DEFAULT_QUESTIONS = createDefaultQuestions();

    private static List<Question> createDefaultQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question("Сколько мне лет?", Arrays.asList("1-10", "11-15", "16-20", "21-99")));
        questions.add(new Question("Какую одежду я ношу?", Arrays.asList("Белую", "Темную", "Цветную", "Гоняю без нее")));
        questions.add(new Question("Важно ли мне мнение?", Arrays.asList("Да", "Нет", "50/50", "Смотря чье")));
        questions.add(new Question("Сколько языков я знаю?", Arrays.asList("1", "2", "3", "Больше 3")));
        questions.add(new Question("Какой мой любимый цвет?", Arrays.asList("Белый", "Черный", "Зеленый", "Другие")));
        questions.add(new Question("Есть ли у меня братья или сестры?", Arrays.asList("Брат/Братья", "Сестра/Сестры", "Брат и сестра", "Нет")));
        questions.add(new Question("Какое мое любимое время дня?", Arrays.asList("Утро", "День", "Вечер", "Ночь")));
        questions.add(new Question("Часто ли я матерюсь?", Arrays.asList("Да", "Нет", "50/50", "Зависит от ситуации")));
        questions.add(new Question("Чего из перечисленного я никогда не боялся?", Arrays.asList("Насекомых", "Высоты", "Темноты", "Другое")));
        questions.add(new Question("Какие волосы мне нравятся?", Arrays.asList("Кудрявые", "Прямые", "Без разницы", "Без волос")));
        questions.add(new Question("Без чего я бы не смог прожить?", Arrays.asList("Телефона", "Сладостей", "Денег", "Ничего из перечисленного")));
        questions.add(new Question("С кем я предпочитаю гулять?", Arrays.asList("Друзья", "Семья", "Партнер", "Один")));
        questions.add(new Question("Какое мое любимое время года?", Arrays.asList("Лето", "Зима", "Весна", "Осень")));
        questions.add(new Question("Если заиграет песня, что я буду делать?", Arrays.asList("Танцевать", "Петь", "Ничего", "Смотря на ситуцию")));
        questions.add(new Question("Какой отдых я предпочитаю?", Arrays.asList("Горы", "Море", "Сидеть дома", "Другое")));

        return questions;
    }

    public String createNewTest(Long creatorId, String creatorName) {
        String testId = UUID.randomUUID().toString().substring(0, 8);

        FriendshipTest test = new FriendshipTest();
        test.setTestId(testId);
        test.setCreatorId(creatorId);
        test.setCreatorName(creatorName);

        List<Question> testQuestions = new ArrayList<>();
        for (Question defaultQuestion : DEFAULT_QUESTIONS) {
            Question questionCopy = new Question(defaultQuestion.getText(),
                    new ArrayList<>(defaultQuestion.getOptions()));
            testQuestions.add(questionCopy);
        }
        test.setQuestions(testQuestions);

        tests.put(testId, test);

        UserSession session = new UserSession();
        session.setUserId(creatorId);
        session.setCurrentTestId(testId);
        session.setCreatingTest(true);
        session.setTakingTest(false);

        userSessions.put(creatorId, session);

        System.out.println("Создан новый тест: " + testId + " для пользователя: " + creatorName);
        return testId;
    }

    public Question getNextQuestion(Long userId) {
        UserSession session = userSessions.get(userId);
        if (session == null || session.getCurrentQuestionIndex() >= DEFAULT_QUESTIONS.size()) {
            return null;
        }

        return DEFAULT_QUESTIONS.get(session.getCurrentQuestionIndex());
    }

    public void saveAnswer(Long userId, String answer) {
        UserSession session = userSessions.get(userId);
        if (session != null) {
            session.getUserAnswers().put(session.getCurrentQuestionIndex(), answer);

            if (session.isCreatingTest()) {
                FriendshipTest test = tests.get(session.getCurrentTestId());
                if (test != null) {
                    test.getQuestions().get(session.getCurrentQuestionIndex()).setCorrectAnswer(answer);
                }
            }

            session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
        }
    }

    public boolean hasCompletedAllQuestions(Long userId) {
        UserSession session = userSessions.get(userId);
        return session != null && session.getCurrentQuestionIndex() >= DEFAULT_QUESTIONS.size();
    }

    public String completeTestCreation(Long userId) {
        UserSession session = userSessions.get(userId);
        if (session != null) {
            session.setCreatingTest(false);
            String testUrl = generateTestUrl(session.getCurrentTestId());
            session.reset();
            return testUrl;
        }
        return null;
    }

    public String generateTestUrl(String testId) {
        return "https://t.me/Test_On_Friends_bot?start=" + testId;
    }

    public FriendshipTest getTest(String testId) {
        if (testId == null) {
            System.out.println("Ошибка: попытка получить тест с null ID");
            return null;
        }
        return tests.get(testId);
    }

    public UserSession getUserSession(Long userId) {
        return userSessions.get(userId);
    }

    public void startTakingTest(Long userId, String testId) {
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setCurrentTestId(testId);
        session.setCreatingTest(false);
        session.setTakingTest(true);

        userSessions.put(userId, session);
    }

    public TestResult calculateResults(Long userId, String testId) {
        if (testId == null) {
            System.out.println("Ошибка: testId is null для пользователя " + userId);
            return null;
        }

        UserSession session = userSessions.get(userId);
        FriendshipTest test = tests.get(testId);

        if (session == null || test == null) {
            System.out.println("Ошибка: сессия или тест не найдены. userId: " + userId + ", testId: " + testId);
            return null;
        }

        int score = 0;
        Map<Integer, String> userAnswers = session.getUserAnswers();

        for (int i = 0; i < test.getQuestions().size(); i++) {
            String userAnswer = userAnswers.get(i);
            String correctAnswer = test.getQuestions().get(i).getCorrectAnswer();

            if (userAnswer != null && userAnswer.equals(correctAnswer)) {
                score++;
            }
        }

        TestResult result = new TestResult(test.getQuestions().size());
        result.setScore(score);
        result.setUserAnswers(new HashMap<>(userAnswers));

        test.getResults().put(userId, result);

        sendResultToCreator(test, userId, result);

        session.reset();

        return result;
    }

    private void sendResultToCreator(FriendshipTest test, Long userId, TestResult result) {
        System.out.println("Результат для создателя " + test.getCreatorName() +
                ": пользователь " + userId + " набрал " + result.getScore() +
                "/" + result.getTotalQuestions() + " (" +
                String.format("%.1f", result.getPercentage()) + "%)");
    }

    public void removeUserSession(Long userId) {
        userSessions.remove(userId);
    }
}