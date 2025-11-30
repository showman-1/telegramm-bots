package org.example.bot;

import org.example.model.FriendshipTest;
import org.example.model.UserSession;
import org.example.model.TestResult;
import org.example.service.TestManager;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import java.util.Map;
import java.util.List;

public class MessageHandler {
    private final TestManager testManager;
    private final ResponseGenerator responseGenerator;

    public MessageHandler(TestManager testManager) {
        this.testManager = testManager;
        this.responseGenerator = new ResponseGenerator();
    }

    public BotResponse handleMessage(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            BotResponse response = handleTextMessage(update);
            return response;
        } else if (update.hasCallbackQuery()) {
            return handleCallbackQuery(update);
        }
        return null;
    }

    private BotResponse handleTextMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        User user = update.getMessage().getFrom();
        Long userId = user.getId();
        String userName = user.getFirstName();

        if (messageText.startsWith("/start")) {
            return handleStartCommand(chatId, userId, userName, messageText);
        } else if (messageText.equals("/create")) {
            return handleCreateCommand(chatId, userId, userName);
        } else if (messageText.equals("/help")) {
            return handleHelpCommand(chatId);
        } else {
            return handleTextAnswer(chatId, userId, messageText);
        }
    }

    private BotResponse handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long userId = update.getCallbackQuery().getFrom().getId();
        String userName = update.getCallbackQuery().getFrom().getFirstName();

        switch (callbackData) {
            case "create_test":
                return handleCreateCommand(chatId, userId, userName);
            case "friends_ranking":
                return handleFriendsRankingCommand(chatId, userId);
            case "cancel":
                return handleCancelCommand(chatId, userId);
            case "help":
                return handleHelpCommand(chatId);
            default:
                if (callbackData.startsWith("answer_")) {
                    String answer = callbackData.substring(7);
                    return handleAnswer(chatId, userId, answer);
                }
                break;
        }
        return null;
    }

    private BotResponse handleStartCommand(Long chatId, Long userId, String userName, String messageText) {
        if (messageText.contains(" ")) {
            String testId = messageText.split(" ")[1];
            return handleStartTakingTest(chatId, userId, userName, testId);
        } else {
            return responseGenerator.createWelcomeResponse(chatId, userName);
        }
    }

    private BotResponse handleCreateCommand(Long chatId, Long userId, String userName) {
        String testId = testManager.createNewTest(userId, userName);
        BotResponse initialResponse = responseGenerator.createTestCreationStartResponse(chatId);
        BotResponse questionResponse = getNextQuestionResponse(chatId, userId);

        return new BotResponse(initialResponse, questionResponse);
    }

    private BotResponse handleHelpCommand(Long chatId) {
        return responseGenerator.createHelpResponse(chatId);
    }

    private BotResponse handleCancelCommand(Long chatId, Long userId) {
        testManager.removeUserSession(userId);
        return responseGenerator.createCancelResponse(chatId);
    }

    private BotResponse handleFriendsRankingCommand(Long chatId, Long userId) {
        // Получаем все тесты пользователя
        List<FriendshipTest> userTests = testManager.getTestsByCreator(userId);

        if (userTests.isEmpty()) {
            return responseGenerator.createNoTestsResponse(chatId);
        }

        // Для простоты берем первый тест (можно расширить для выбора конкретного теста)
        FriendshipTest test = userTests.get(0);
        List<Map.Entry<Long, TestResult>> ranking = testManager.getFriendsRanking(test.getTestId());

        return responseGenerator.createFriendsRankingResponse(chatId, test, ranking);
    }

    private BotResponse handleStartTakingTest(Long chatId, Long userId, String userName, String testId) {
        FriendshipTest test = testManager.getTest(testId);
        if (test == null) {
            return responseGenerator.createTestNotFoundResponse(chatId);
        }

        // Сохраняем имя друга
        test.addFriendName(userId, userName);

        testManager.startTakingTest(userId, testId);
        BotResponse startResponse = responseGenerator.createTestTakingStartResponse(chatId, test.getCreatorName());
        BotResponse questionResponse = getNextQuestionResponse(chatId, userId);

        return new BotResponse(startResponse, questionResponse);
    }

    private BotResponse handleAnswer(Long chatId, Long userId, String answer) {
        testManager.saveAnswer(userId, answer);

        if (testManager.hasCompletedAllQuestions(userId)) {
            return completeCurrentTest(chatId, userId);
        } else {
            return getNextQuestionResponse(chatId, userId);
        }
    }

    private BotResponse handleTextAnswer(Long chatId, Long userId, String answer) {
        UserSession session = testManager.getUserSession(userId);

        if (session != null && (session.isCreatingTest() || session.isTakingTest())) {
            return null;
        }

        return responseGenerator.createDefaultResponse(chatId);
    }

    private BotResponse getNextQuestionResponse(Long chatId, Long userId) {
        return responseGenerator.createQuestionResponse(chatId, userId, testManager);
    }

    private BotResponse completeCurrentTest(Long chatId, Long userId) {
        var session = testManager.getUserSession(userId);
        if (session == null) {
            return responseGenerator.createErrorResponse(chatId, "Сессия не найдена");
        }

        if (session.isCreatingTest()) {
            String testUrl = testManager.completeTestCreation(userId);
            return responseGenerator.createTestCreationCompleteResponse(chatId, testUrl);
        } else {
            String testId = session.getCurrentTestId();
            if (testId == null) {
                return responseGenerator.createErrorResponse(chatId, "ID теста не найден");
            }

            FriendshipTest test = testManager.getTest(testId);
            if (test == null) {
                return responseGenerator.createErrorResponse(chatId, "Тест не найден");
            }

            TestResult result = testManager.calculateResults(userId, testId);
            if (result == null) {
                return responseGenerator.createErrorResponse(chatId, "Ошибка расчета результатов");
            }

            BotResponse userResponse = responseGenerator.createTestResultResponse(chatId, result, test.getCreatorName());
            BotResponse creatorResponse = responseGenerator.createCreatorNotificationResponse(test, userId, result);

            return new BotResponse(userResponse, creatorResponse);
        }
    }
}