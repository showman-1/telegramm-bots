package org.example.bot;

import org.example.model.*;
import org.example.service.TestManager;
import org.example.util.KeyboardHelper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Главный класс Telegram бота.
 * Обрабатывает все входящие сообщения и команды.
 * Наследуется от TelegramLongPollingBot - стандартного класса для ботов.
 */
public class FriendshipTestBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(FriendshipTestBot.class);

    private final TestManager testManager;

    // Конструктор
    public FriendshipTestBot() {
        this.testManager = new TestManager();
        logger.info("Бот инициализирован");
    }

    /**
     * ГЛАВНЫЙ МЕТОД - обрабатывает все обновления от Telegram
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            // Обрабатываем текстовые сообщения
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update);
            }
            // Обрабатываем нажатия на inline-кнопки
            else if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
            }
        } catch (Exception e) {
            logger.error("Ошибка при обработке обновления", e);
        }
    }

    /**
     * Обрабатывает текстовые сообщения от пользователей
     */
    private void handleMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        User user = update.getMessage().getFrom();
        Long userId = user.getId();
        String userName = user.getFirstName();

        logger.info("Получено сообщение от {} ({}): {}", userName, userId, messageText);

        // Обрабатываем команду /start
        if (messageText.startsWith("/start")) {
            handleStartCommand(chatId, userId, userName, messageText);
        }
        // Обрабатываем команду /create
        else if (messageText.equals("/create")) {
            startTestCreation(chatId, userId, userName);
        }
        // Обрабатываем команду /help
        else if (messageText.equals("/help")) {
            sendHelpMessage(chatId);
        }
        // Обрабатываем обычные текстовые сообщения (ответы на вопросы)
        else {
            handleTextAnswer(chatId, userId, messageText);
        }
    }

    /**
     * Обрабатывает команду /start
     */
    private void handleStartCommand(Long chatId, Long userId, String userName, String messageText) {
        // Если команда содержит параметр (например, /start ABC123)
        if (messageText.contains(" ")) {
            String testId = messageText.split(" ")[1];
            startTakingTest(chatId, userId, userName, testId);
        } else {
            // Просто /start - показываем главное меню
            sendWelcomeMessage(chatId, userName);
        }
    }

    /**
     * Обрабатывает нажатия на inline-кнопки
     */
    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long userId = update.getCallbackQuery().getFrom().getId();
        String userName = update.getCallbackQuery().getFrom().getFirstName();

        logger.info("Обработка callback от {}: {}", userName, callbackData);

        // Обрабатываем создание теста
        if (callbackData.equals("create_test")) {
            startTestCreation(chatId, userId, userName);
        }
        // Обрабатываем ответы на вопросы
        else if (callbackData.startsWith("answer_")) {
            String answer = callbackData.substring(7); // Убираем "answer_"
            handleAnswer(chatId, userId, answer);
        }
        // Обрабатываем отмену
        else if (callbackData.equals("cancel")) {
            handleCancel(chatId, userId);
        }
        // Обрабатываем помощь
        else if (callbackData.equals("help")) {
            sendHelpMessage(chatId);
        }
    }

    /**
     * Отправляет приветственное сообщение
     */
    private void sendWelcomeMessage(Long chatId, String userName) {
        String text = "👋 Привет, " + userName + "!\n\n" +
                "Добро пожаловать в бот 'Тест на дружбу'! 🎯\n\n" +
                "Здесь ты можешь:\n" +
                "• 📝 Создать свой тест с 15 вопросами о себе\n" +
                "• 🔗 Получить ссылку для друзей\n" +
                "• 🎯 Узнать, насколько хорошо друзья тебя знают\n\n" +
                "Выбери действие:";

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());

        executeMessage(message);
    }

    /**
     * Начинает процесс создания теста
     */
    private void startTestCreation(Long chatId, Long userId, String userName) {
        // Создаем новый тест
        String testId = testManager.createNewTest(userId, userName);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🎉 Отлично! Ты начал создание теста на дружбу!\n\n" +
                "Я буду задавать тебе 15 вопросов о себе. " +
                "Выбирай те варианты ответов, которые больше всего тебе подходят.\n\n" +
                "Давай начнем! ✨");

        executeMessage(message);

        // Отправляем первый вопрос
        sendNextQuestion(chatId, userId);
    }

    /**
     * Отправляет следующий вопрос пользователю
     */
    private void sendNextQuestion(Long chatId, Long userId) {
        // Получаем следующий вопрос
        Question question = testManager.getNextQuestion(userId);

        // Если вопросы закончились
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

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(KeyboardHelper.createOptionsKeyboard(question.getOptions()));

        executeMessage(message);
    }

    /**
     * Обрабатывает ответ пользователя на вопрос
     */
    private void handleAnswer(Long chatId, Long userId, String answer) {
        // Сохраняем ответ
        testManager.saveAnswer(userId, answer);

        // Проверяем, завершил ли пользователь все вопросы
        if (testManager.hasCompletedAllQuestions(userId)) {
            UserSession session = testManager.getUserSession(userId);
            if (session != null && session.isCreatingTest()) {
                completeTestCreation(chatId, userId);
            } else {
                completeTestTaking(chatId, userId);
            }
        } else {
            // Отправляем следующий вопрос
            sendNextQuestion(chatId, userId);
        }
    }

    /**
     * Завершает создание теста и отправляет ссылку
     */
    private void completeTestCreation(Long chatId, Long userId) {
        // Генерируем ссылку на тест
        String testUrl = testManager.completeTestCreation(userId);

        String text = "🎉 Поздравляю! Ты создал тест на дружбу!\n\n" +
                "Теперь отправь эту ссылку друзьям:\n\n" +
                "🔗 " + testUrl + "\n\n" +
                "Когда друзья пройдут твой тест, ты увидишь результаты! 📊";

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());

        executeMessage(message);
    }

    /**
     * Начинает прохождение теста
     */
    private void startTakingTest(Long chatId, Long userId, String userName, String testId) {
        FriendshipTest test = testManager.getTest(testId);

        if (test == null) {
            sendMessage(chatId, "❌ Тест не найден! Возможно, ссылка устарела или неверна.");
            return;
        }

        // Начинаем прохождение теста
        testManager.startTakingTest(userId, testId);

        String text = "🎯 Ты начал тест на дружбу от " + test.getCreatorName() + "!\n\n" +
                "Отвечай на вопросы так, как думаешь, что ответил бы твой друг.\n\n" +
                "Удачи! 🍀";

        sendMessage(chatId, text);
        sendNextQuestion(chatId, userId);
    }

    /**
     * Завершает прохождение теста и показывает результаты
     */
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

        // Добавляем оценку в зависимости от результата
        if (result.getPercentage() >= 80) {
            text += "🎉 Отлично! Ты настоящий друг! 💖";
        } else if (result.getPercentage() >= 60) {
            text += "👍 Хорошо! Ты хорошо знаешь друга! 😊";
        } else if (result.getPercentage() >= 40) {
            text += "🤔 Неплохо, но есть куда стремиться! 📚";
        } else {
            text += "😅 Похоже, нужно больше общаться! 💬";
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());

        executeMessage(message);

        // Отправляем результат создателю теста
        sendResultToCreator(test, userId, result);
    }

    /**
     * Отправляет результат создателю теста
     */
    private void sendResultToCreator(FriendshipTest test, Long userId, TestResult result) {
        try {
            String creatorText = "📊 Кто-то прошел ваш тест!\n\n" +
                    "✅ Правильных ответов: " + result.getScore() + "/" + result.getTotalQuestions() + "\n" +
                    "📈 Процент правильных: " + String.format("%.1f", result.getPercentage()) + "%";

            SendMessage message = new SendMessage();
            message.setChatId(test.getCreatorId().toString());
            message.setText(creatorText);

            executeMessage(message);
        } catch (Exception e) {
            logger.error("Ошибка при отправке результата создателю теста", e);
        }
    }

    /**
     * Обрабатывает отмену действия
     */
    private void handleCancel(Long chatId, Long userId) {
        UserSession session = testManager.getUserSession(userId);
        if (session != null) {
            session.reset();
        }

        sendMessage(chatId, "❌ Действие отменено.");
        sendWelcomeMessage(chatId, "друг");
    }

    /**
     * Отправляет справку
     */
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

    /**
     * Обрабатывает текстовые ответы (не из кнопок)
     */
    private void handleTextAnswer(Long chatId, Long userId, String answer) {
        // Проверяем, находится ли пользователь в процессе теста
        UserSession session = testManager.getUserSession(userId);
        if (session != null && (session.isCreatingTest() || session.isTakingTest())) {
            handleAnswer(chatId, userId, answer);
        } else {
            sendMessage(chatId, "Используй кнопки для навигации или /start для главного меню");
        }
    }

    /**
     * Утилитный метод для отправки простого сообщения
     */
    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        executeMessage(message);
    }

    /**
     * Утилитный метод для выполнения отправки сообщения
     */
    private void executeMessage(SendMessage message) {
        try {
            execute(message);
            logger.info("Сообщение отправлено в чат {}", message.getChatId());
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке сообщения", e);
        }
    }

    /**
     * Возвращает имя бота (заменить на настоящее имя бота)
     */
    @Override
    public String getBotUsername() {
        return "Test_On_Friends_bot"; // ЗАМЕНИТЕ на имя вашего бота
    }

    /**
     * Возвращает токен бота (получить у @BotFather)
     */
    @Override
    public String getBotToken() {
        return "8009528820:AAFMq2CtDeB3BwMAB4Ve4qN_rlzydVXHtI0"; // ЗАМЕНИТЕ на токен вашего бота
    }
}