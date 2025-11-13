package org.example.model;

import java.util.Map;

/**
 * Класс представляет результат прохождения теста.
 * Хранит количество правильных ответов и ответы пользователя.
 */
public class TestResult {
    private int score;                   // Количество правильных ответов
    private int totalQuestions;          // Общее количество вопросов
    private Map<Integer, String> userAnswers;  // Ответы пользователя (номер вопроса -> ответ)
    private long completedAt;            // Время завершения теста

    public TestResult(int totalQuestions) {
        this.totalQuestions = totalQuestions;
        this.completedAt = System.currentTimeMillis();
    }

    // Геттеры и сеттеры
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public Map<Integer, String> getUserAnswers() { return userAnswers; }
    public void setUserAnswers(Map<Integer, String> userAnswers) { this.userAnswers = userAnswers; }

    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }

    /**
     * Рассчитывает процент правильных ответов
     */
    public double getPercentage() {
        return (double) score / totalQuestions * 100;
    }
}