package org.example.service;

import org.example.model.*;
import org.example.util.UrlGenerator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.Map.Entry;

public class TestManager {
    private final Map<String, FriendshipTest> tests = new ConcurrentHashMap<>();
    private final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();
    private final ResultNotifier resultNotifier;

    public TestManager() {
        this.resultNotifier = new ResultNotifier();
    }

    public String createNewTest(Long creatorId, String creatorName) {
        String testId = UrlGenerator.generateTestId();
        FriendshipTest test = new FriendshipTest(testId, creatorId, creatorName);
        test.setQuestions(QuestionProvider.getDefaultQuestions());
        tests.put(testId, test);

        UserSession session = new UserSession(creatorId, testId, true, false);
        userSessions.put(creatorId, session);

        return testId;
    }

    public Question getNextQuestion(Long userId) {
        UserSession session = userSessions.get(userId);
        if (session == null || session.getCurrentQuestionIndex() >= QuestionProvider.getTotalQuestions()) {
            return null;
        }
        return QuestionProvider.getDefaultQuestions().get(session.getCurrentQuestionIndex());
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
        return session != null && session.getCurrentQuestionIndex() >= QuestionProvider.getTotalQuestions();
    }

    public String completeTestCreation(Long userId) {
        UserSession session = userSessions.get(userId);
        if (session != null) {
            session.setCreatingTest(false);
            String testUrl = UrlGenerator.generateTestUrl(session.getCurrentTestId());
            userSessions.remove(userId);
            return testUrl;
        }
        return null;
    }

    public FriendshipTest getTest(String testId) {
        return tests.get(testId);
    }

    public UserSession getUserSession(Long userId) {
        return userSessions.get(userId);
    }

    public void startTakingTest(Long userId, String testId) {
        UserSession session = new UserSession(userId, testId, false, true);
        userSessions.put(userId, session);
    }

    public TestResult calculateResults(Long userId, String testId) {
        if (testId == null) {
            return null;
        }

        UserSession session = userSessions.get(userId);
        FriendshipTest test = tests.get(testId);

        if (session == null || test == null) {
            return null;
        }

        int score = calculateScore(session, test);
        TestResult result = createTestResult(test, session, score);

        test.getResults().put(userId, result);
        resultNotifier.notifyCreator(test, userId, result);
        userSessions.remove(userId);

        return result;
    }

    private int calculateScore(UserSession session, FriendshipTest test) {
        int score = 0;
        Map<Integer, String> userAnswers = session.getUserAnswers();

        for (int i = 0; i < test.getQuestions().size(); i++) {
            String userAnswer = userAnswers.get(i);
            String correctAnswer = test.getQuestions().get(i).getCorrectAnswer();

            if (userAnswer != null && userAnswer.equals(correctAnswer)) {
                score++;
            }
        }
        return score;
    }

    private TestResult createTestResult(FriendshipTest test, UserSession session, int score) {
        TestResult result = new TestResult(test.getQuestions().size());
        result.setScore(score);
        result.setUserAnswers(new HashMap<>(session.getUserAnswers()));
        return result;
    }

    public void removeUserSession(Long userId) {
        userSessions.remove(userId);
    }

    public List<Entry<Long, TestResult>> getFriendsRanking(String testId) {
        FriendshipTest test = tests.get(testId);
        if (test == null || test.getResults().isEmpty()) {
            return new ArrayList<>();
        }

        // Сортируем результаты по убыванию процента правильных ответов
        return test.getResults().entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue().getPercentage(), e1.getValue().getPercentage()))
                .collect(Collectors.toList());
    }

    public List<FriendshipTest> getTestsByCreator(Long creatorId) {
        return tests.values().stream()
                .filter(test -> test.getCreatorId().equals(creatorId))
                .collect(Collectors.toList());
    }
}