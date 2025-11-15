package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class UserSession{
    private Long userId;
    private String currentTestId;
    private int currentQuestionIndex;
    private Map<Integer, String> userAnswers;
    private boolean creatingTest;
    private boolean takingTest;

    public UserSession() {
        this.userAnswers = new HashMap<>();
        this.currentQuestionIndex = 0;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCurrentTestId() {
        return currentTestId;
    }
    public void setCurrentTestId(String currentTestId) {
        this.currentTestId = currentTestId;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public Map<Integer, String> getUserAnswers() {
        return userAnswers;
    }
    public void setUserAnswers(Map<Integer, String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public boolean isCreatingTest() {
        return creatingTest;
    }
    public void setCreatingTest(boolean creatingTest) {
        this.creatingTest = creatingTest;
    }

    public boolean isTakingTest() {
        return takingTest;
    }
    public void setTakingTest(boolean takingTest) {
        this.takingTest = takingTest;
    }

    public void reset() {
        this.currentQuestionIndex = 0;
        this.userAnswers.clear();
        this.takingTest = false;
        this.creatingTest = false;
        this.currentTestId = null;
    }
}