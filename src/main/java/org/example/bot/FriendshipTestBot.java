package org.example.bot;

import org.example.model.*;
import org.example.service.TestManager;
import org.example.util.KeyboardHelper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class FriendshipTestBot extends TelegramLongPollingBot {

    private final TestManager testManager;

    public FriendshipTestBot() {
        this.testManager = new TestManager();
    }

    @Override
    public String getBotUsername() {
        return "Test_On_Friends_bot";
    }

    @Override
    public String getBotToken() {
        return "8009528820:AAFMq2CtDeB3BwMAB4Ve4qN_rlzydVXHtI0";
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        executeMessage(message);
    }

    private void sendWelcomeMessage(Long chatId, String userName) {
        String text = "👋 Привет, " + userName + "!\n\n" +
                "Добро пожаловать в бот 'Тест на дружбу'! 🎯\n\n" +
                "Здесь ты можешь:\n" +
                "• 📝 Создать свой тест с 15 вопросами о себе\n" +
                "• 🔗 Получить ссылку для друзей\n" +
                "• 🎯 Узнать, насколько хорошо друзья тебя знают\n\n" +
                "Выбери действие:";

        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());

        executeMessage(message);
    }

    private void sendHelpMessage(Long chatId) {
        String text = "❓ Помощь по боту 'Тест на дружбу'\n\n" +
                "📝 Как создать тест:\n" +
                "1. Нажми 'Создать тест'\n" +
                "2. Ответь на 15 вопросов о себе\n" +
                "3. Получи ссылку для друзей\n\n" +
                "🎯 Как пройти тест:\n" +
                "1. Перейди по ссылке от друга\n" +
                "2. Ответь на вопросы так, как думаешь ответил бы твой друг\n" +
                "3. Узнай результат\n\n" +
                "⚡ Команды:\n" +
                "/start - главное меню\n" +
                "/create - создать тест\n" +
                "/help - эта справка";

        sendMessage(chatId, text);
    }

    private void sendResultToCreator(FriendshipTest test, Long userId, TestResult result) {
        try {
            String creatorText = "📊 Кто-то прошел ваш тест!\n\n" +
                    "✅ Правильных ответов: " + result.getScore() + "/" + result.getTotalQuestions() + "\n" +
                    "📈 Процент правильных: " + String.format("%.1f", result.getPercentage()) + "%";

            SendMessage message = new SendMessage(test.getCreatorId().toString(), creatorText);
            executeMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNextQuestion(Long chatId, Long userId) {
        Question question = testManager.getNextQuestion(userId);

        if (question == null) {
            UserSession session = testManager.getUserSession(userId);
            if (session != null && session.isCreatingTest()) {
                completeTestCreation(chatId, userId);
            } else {
                completeTestTaking(chatId, userId);
            }
            return;
        }

        UserSession session = testManager.getUserSession(userId);
        int questionNumber = session.getCurrentQuestionIndex() + 1;
        int totalQuestions = 15;

        String text = "❓ Вопрос " + questionNumber + "/" + totalQuestions + ":\n" +
                question.getText();

        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(KeyboardHelper.createOptionsKeyboard(question.getOptions()));

        executeMessage(message);
    }

    private void handleAnswer(Long chatId, Long userId, String answer) {
        testManager.saveAnswer(userId, answer);

        if (testManager.hasCompletedAllQuestions(userId)) {
            UserSession session = testManager.getUserSession(userId);
            if (session != null && session.isCreatingTest()) {
                completeTestCreation(chatId, userId);
            } else {
                completeTestTaking(chatId, userId);
            }
        } else {
            sendNextQuestion(chatId, userId);
        }
    }

    private void handleStartCommand(Long chatId, Long userId, String userName, String messageText) {
        if (messageText.contains(" ")) {
            String testId = messageText.split(" ")[1];
            startTakingTest(chatId, userId, userName, testId);
        } else {
            sendWelcomeMessage(chatId, userName);
        }
    }

    private void startTestCreation(Long chatId, Long userId, String userName) {
        String testId = testManager.createNewTest(userId, userName);

        SendMessage message = new SendMessage(chatId.toString(),
                "🎉 Отлично! Ты начал создание теста на дружбу!\n\n" +
                        "Я буду задавать тебе 15 вопросов о себе. " +
                        "Выбирай те варианты ответов, которые больше всего тебе подходят.\n\n" +
                        "Давай начнем! ✨");

        executeMessage(message);

        sendNextQuestion(chatId, userId);
    }

    private void startTakingTest(Long chatId, Long userId, String userName, String testId) {
        FriendshipTest test = testManager.getTest(testId);

        if (test == null) {
            sendMessage(chatId, "❌ Тест не найден! Возможно, ссылка устарела или неверна.");
            return;
        }

        testManager.startTakingTest(userId, testId);

        String text = "🎯 Ты начал тест на дружбу от " + test.getCreatorName() + "!\n\n" +
                "Отвечай на вопросы так, как думаешь, что ответил бы твой друг.\n\n" +
                "Удачи! 🍀";

        sendMessage(chatId, text);
        sendNextQuestion(chatId, userId);
    }

    private void completeTestCreation(Long chatId, Long userId) {
        String testUrl = testManager.completeTestCreation(userId);

        String text = "🎉 Поздравляю! Ты создал тест на дружбу!\n\n" +
                "Теперь отправь эту ссылку друзьям:\n\n" +
                "🔗 " + testUrl + "\n\n" +
                "Когда друзья пройдут твой тест, ты увидишь результаты! 📊";

        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());

        executeMessage(message);
    }

    private void completeTestTaking(Long chatId, Long userId) {
        UserSession session = testManager.getUserSession(userId);
        if (session == null) {
            sendMessage(chatId, "❌ Ошибка: сессия не найдена.");
            return;
        }

        String testId = session.getCurrentTestId();
        if (testId == null) {
            sendMessage(chatId, "❌ Ошибка: ID теста не найден.");
            return;
        }

        TestResult result = testManager.calculateResults(userId, testId);
        FriendshipTest test = testManager.getTest(testId);

        if (result == null || test == null) {
            sendMessage(chatId, "❌ Произошла ошибка при расчете результатов.");
            return;
        }

        String text = "📊 Результаты теста от " + test.getCreatorName() + ":\n\n" +
                "✅ Правильных ответов: " + result.getScore() + "/" + result.getTotalQuestions() + "\n" +
                "📈 Процент правильных: " + String.format("%.1f", result.getPercentage()) + "%\n\n";

        if (result.getPercentage() >= 80) {
            text += "🎉 Отлично! Ты настоящий друг! 💖";
        } else if (result.getPercentage() >= 60) {
            text += "👍 Хорошо! Ты хорошо знаешь друга! 😊";
        } else if (result.getPercentage() >= 40) {
            text += "🤔 Неплохо, но есть куда стремиться! 📚";
        } else {
            text += "😅 Похоже, нужно больше общаться! 💬";
        }

        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());

        executeMessage(message);

        sendResultToCreator(test, userId, result);
    }

    private void handleCancel(Long chatId, Long userId) {
        UserSession session = testManager.getUserSession(userId);
        if (session != null) {
            session.reset();
        }

        sendMessage(chatId, "❌ Действие отменено.");
        sendWelcomeMessage(chatId, "друг");
    }

    private void handleTextAnswer(Long chatId, Long userId, String answer) {
        UserSession session = testManager.getUserSession(userId);
        if (session != null && (session.isCreatingTest() || session.isTakingTest())) {
            handleAnswer(chatId, userId, answer);
        } else {
            sendMessage(chatId, "Используй кнопки для навигации или /start для главного меню");
        }
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long userId = update.getCallbackQuery().getFrom().getId();
        String userName = update.getCallbackQuery().getFrom().getFirstName();

        switch (callbackData) {
            case "create_test":
                startTestCreation(chatId, userId, userName);
                break;
            case "cancel":
                handleCancel(chatId, userId);
                break;
            case "help":
                sendHelpMessage(chatId);
                break;
            default:
                if (callbackData.startsWith("answer_")) {
                    String answer = callbackData.substring(7);
                    handleAnswer(chatId, userId, answer);
                }
                break;
        }
    }

    private void handleMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        User user = update.getMessage().getFrom();
        Long userId = user.getId();
        String userName = user.getFirstName();

        if (messageText.startsWith("/start")) {
            handleStartCommand(chatId, userId, userName, messageText);
        }
        else if (messageText.equals("/create")) {
            startTestCreation(chatId, userId, userName);
        }
        else if (messageText.equals("/help")) {
            sendHelpMessage(chatId);
        }
        else {
            handleTextAnswer(chatId, userId, messageText);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update);
            }
            else if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}