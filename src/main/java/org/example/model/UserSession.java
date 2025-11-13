package org.example.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс отслеживает текущее состояние пользователя.
 * Помогает управлять процессом создания/прохождения теста.
 */
public class UserSession {
    private Long userId;                    // ID пользователя
    private String currentTestId;           // ID текущего теста
    private int currentQuestionIndex;       // Текущий номер вопроса (0-14)
    private Map<Integer, String> userAnswers; // Ответы пользователя
    private boolean creatingTest;           // true = создает тест, false = проходит тест
    private boolean takingTest;             // Находится ли в процессе прохождения

    public UserSession() {
        this.userAnswers = new HashMap<>();
        this.currentQuestionIndex = 0;
    }

    // Геттеры и сеттеры
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCurrentTestId() { return currentTestId; }
    public void setCurrentTestId(String currentTestId) { this.currentTestId = currentTestId; }

    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public Map<Integer, String> getUserAnswers() { return userAnswers; }
    public void setUserAnswers(Map<Integer, String> userAnswers) { this.userAnswers = userAnswers; }

    public boolean isCreatingTest() { return creatingTest; }
    public void setCreatingTest(boolean creatingTest) { this.creatingTest = creatingTest; }

    public boolean isTakingTest() { return takingTest; }
    public void setTakingTest(boolean takingTest) { this.takingTest = takingTest; }

    /**
     * Сбрасывает сессию к начальному состоянию
     */
    public void reset() {
        this.currentQuestionIndex = 0;
        this.userAnswers.clear();
        this.creatingTest = false;
        this.takingTest = false;
        this.currentTestId = null;
    }
}