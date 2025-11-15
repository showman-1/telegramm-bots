package org.example.model;

import java.util.Map;

public class TestResult {
    private int score;
    private int totalQuestions;
    private Map<Integer, String> userAnswers;
    private long completedAt;

    public TestResult(int totalQuestions) {
        this.totalQuestions = totalQuestions;
        this.completedAt = System.currentTimeMillis();
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Map<Integer, String> getUserAnswers() {
        return userAnswers;
    }
    public void setUserAnswers(Map<Integer, String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public long getCompletedAt() {
        return completedAt;
    }
    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }
    public double getPercentage() {
        return (double) score / totalQuestions * 100;
    }
}