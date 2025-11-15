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

        questions.add(new Question("Какой мой любимый цвет?",
                Arrays.asList("Красный", "Синий", "Зеленый", "Желтый")));

        questions.add(new Question("Какое мое любимое время года?",
                Arrays.asList("Весна", "Лето", "Осень", "Зима")));

        questions.add(new Question("Какой отдых я предпочитаю?",
                Arrays.asList("Горы", "Море", "Городской туризм", "Отдых на даче")));

        questions.add(new Question("Какой жанр музыки я люблю?",
                Arrays.asList("Поп", "Рок", "Классика", "Хип-хоп")));

        questions.add(new Question("Какой мой любимый фильм?",
                Arrays.asList("Комедия", "Драма", "Боевик", "Фантастика")));

        questions.add(new Question("Что я выберу для вечера?",
                Arrays.asList("Кино", "Ресторан", "Прогулка", "Домашний ужин")));

        questions.add(new Question("Мое отношение к спорту?",
                Arrays.asList("Активный спортсмен", "Иногда занимаюсь", "Только смотрю", "Не интересуюсь")));

        questions.add(new Question("Как я провожу выходные?",
                Arrays.asList("Активный отдых", "Встречи с друзьями", "Домашний уют", "Работа/хобби")));

        questions.add(new Question("Мое любимое блюдо?",
                Arrays.asList("Пицца", "Суши", "Стейк", "Салаты")));

        questions.add(new Question("Какой напиток я предпочитаю?",
                Arrays.asList("Кофе", "Чай", "Сок", "Газировка")));

        questions.add(new Question("Мое хобби?",
                Arrays.asList("Чтение", "Спорт", "Творчество", "Путешествия")));

        questions.add(new Question("Как я веду себя в компании?",
                Arrays.asList("Душа компании", "Слушатель", "Организатор", "Наблюдатель")));

        questions.add(new Question("Мое отношение к животным?",
                Arrays.asList("Обожаю животных", "Нравятся некоторые", "Нейтральное", "Не люблю")));

        questions.add(new Question("Что для меня важнее в дружбе?",
                Arrays.asList("Честность", "Поддержка", "Общие интересы", "Времяпровождение")));

        questions.add(new Question("Как я принимаю решения?",
                Arrays.asList("Быстро и уверенно", "Долго обдумываю", "Советуюсь с друзьями", "По настроению")));

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