package org.example.service;

import org.example.model.FriendshipTest;
import org.example.model.TestResult;

public class ResultNotifier {

    public void notifyCreator(FriendshipTest test, Long userId, TestResult result) {
        System.out.println("Результат для создателя " + test.getCreatorName() +
                ": пользователь " + userId + " набрал " + result.getScore() +
                "/" + result.getTotalQuestions() + " (" +
                String.format("%.1f", result.getPercentage()) + "%)");
    }
}