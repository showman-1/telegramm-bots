package org.example.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс представляет весь тест на дружбу.
 * Хранит информацию о создателе, вопросы и результаты прохождений.
 */
public class FriendshipTest {
    private String testId;                    // Уникальный ID теста
    private Long creatorId;                   // ID пользователя, создавшего тест
    private String creatorName;               // Имя создателя теста
    private List<Question> questions;         // Список вопросов
    private Map<Long, TestResult> results;    // Результаты прохождений (userId -> результат)
    private long createdAt;                   // Время создания теста

    public FriendshipTest() {
        this.results = new ConcurrentHashMap<>();
        this.createdAt = System.currentTimeMillis();
    }

    // Геттеры и сеттеры
    public String getTestId() { return testId; }
    public void setTestId(String testId) { this.testId = testId; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public Map<Long, TestResult> getResults() { return results; }
    public void setResults(Map<Long, TestResult> results) { this.results = results; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}