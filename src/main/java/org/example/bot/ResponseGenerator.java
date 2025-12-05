package org.example.bot;

import org.example.model.FriendshipTest;
import org.example.model.TestResult;
import org.example.service.TestManager;
import org.example.util.KeyboardHelper;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.Map;
import java.util.List;

public class ResponseGenerator {

    public BotResponse createWelcomeResponse(Long chatId, String userName) {
        StringBuilder text = new StringBuilder();
        text.append(String.format("👋 Привет, %s!%n%n", userName));
        text.append("Добро пожаловать в бот 'Тест на дружбу'! 🎯%n%n");
        text.append("Здесь ты можешь:%n");
        text.append("• 📝 Создать свой тест с 15 вопросами о себе%n");
        text.append("• 🔗 Получить ссылку для друзей%n");
        text.append("• 🎯 Узнать, насколько хорошо друзья тебя знают%n");
        text.append("• 🏆 Смотреть рейтинг друзей%n%n");
        text.append("Выбери действие:");

        SendMessage message = new SendMessage(chatId.toString(), String.format(text.toString()));
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());
        return new BotResponse(message);
    }

    public BotResponse createHelpResponse(Long chatId) {
        StringBuilder text = new StringBuilder();
        text.append("❓ Помощь по боту 'Тест на дружбу'%n%n");
        text.append("📝 Как создать тест:%n");
        text.append("1. Нажми 'Создать тест'%n");
        text.append("2. Ответь на 15 вопросов о себе%n");
        text.append("3. Получи ссылку для друзей%n%n");
        text.append("🎯 Как пройти тест:%n");
        text.append("1. Перейди по ссылке от друга%n");
        text.append("2. Ответь на вопросы так, как думаешь ответил бы твой друг%n");
        text.append("3. Узнай результат%n%n");
        text.append("🏆 Рейтинг друзей:%n");
        text.append("• Смотри, кто из друзей лучше тебя знает%n");
        text.append("• Топ 10 результатов с процентами%n");
        text.append("• Автоматическое обновление%n%n");
        text.append("⚡ Команды:%n");
        text.append("/start - главное меню%n");
        text.append("/create - создать тест%n");
        text.append("/help - эта справка");

        return new BotResponse(new SendMessage(chatId.toString(), String.format(text.toString())));
    }

    public BotResponse createTestCreationStartResponse(Long chatId) {
        StringBuilder text = new StringBuilder();
        text.append("🎉 Отлично! Ты начал создание теста на дружбу!%n%n");
        text.append("Я буду задавать тебе 15 вопросов о себе. ");
        text.append("Выбирай те варианты ответов, которые больше всего тебе подходят.%n%n");
        text.append("Давай начнем! ✨");

        return new BotResponse(new SendMessage(chatId.toString(), String.format(text.toString())));
    }

    public BotResponse createTestTakingStartResponse(Long chatId, String creatorName) {
        StringBuilder text = new StringBuilder();
        text.append(String.format("🎯 Ты начал тест на дружбу от %s!%n%n", creatorName));
        text.append("Отвечай на вопросы так, как думаешь, что ответил бы твой друг.%n%n");
        text.append("Удачи! 🍀");

        return new BotResponse(new SendMessage(chatId.toString(), String.format(text.toString())));
    }

    public BotResponse createQuestionResponse(Long chatId, Long userId, TestManager testManager) {
        var question = testManager.getNextQuestion(userId);
        if (question == null) {
            return null;
        }

        var session = testManager.getUserSession(userId);
        int questionNumber = session.getCurrentQuestionIndex() + 1;
        int totalQuestions = 15;

        String caption = String.format("❓ Вопрос %d/%d:%n%s", questionNumber, totalQuestions, question.getText());

        SendPhoto photoMessage = new SendPhoto();
        photoMessage.setChatId(chatId.toString());

        File imageFile = new File(question.getImagePath());
        InputFile photo = new InputFile(imageFile);
        photoMessage.setPhoto(photo);
        photoMessage.setCaption(caption);
        photoMessage.setReplyMarkup(KeyboardHelper.createOptionsKeyboard(question.getOptions()));

        return new BotResponse(photoMessage);
    }

    public BotResponse createTestCreationCompleteResponse(Long chatId, String testUrl) {
        StringBuilder text = new StringBuilder();
        text.append("🎉 Поздравляю! Ты создал тест на дружбу!%n%n");
        text.append("Теперь отправь эту ссылку друзьям:%n%n");
        text.append(String.format("🔗 %s%n%n", testUrl));
        text.append("Когда друзья пройдут твой тест, ты увидишь результаты в разделе '🏆 Рейтинг друзей'! 📊");

        SendMessage message = new SendMessage(chatId.toString(), text.toString());
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());
        return new BotResponse(message);
    }

    public BotResponse createTestResultResponse(Long chatId, TestResult result, String creatorName) {
        StringBuilder text = new StringBuilder();
        text.append(String.format("📊 Результаты теста от %s:%n%n", creatorName));
        text.append(String.format("✅ Правильных ответов: %d/%d%n", result.getScore(), result.getTotalQuestions()));
        text.append(String.format("📈 Процент правильных: %.1f%%%n%n", result.getPercentage()));

        double percentage = result.getPercentage();
        String additionalText;

        if (percentage >= 80) {
            additionalText = "🎉 Отлично! Ты настоящий друг! 💖";
        } else if (percentage >= 60) {
            additionalText = "👍 Хорошо! Ты хорошо знаешь друга! 😊";
        } else if (percentage >= 40) {
            additionalText = "🤔 Неплохо, но есть куда стремиться! 📚";
        } else {
            additionalText = "😅 Похоже, нужно больше общаться! 💬";
        }

        text.append(additionalText);
        SendMessage message = new SendMessage(chatId.toString(), text.toString());
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());
        return new BotResponse(message);
    }

    public BotResponse createCreatorNotificationResponse(FriendshipTest test, Long userId, TestResult result) {
        StringBuilder creatorText = new StringBuilder();
        String friendName = test.getFriendName(userId);

        creatorText.append(String.format("📊 %s прошел ваш тест!%n%n", friendName));
        creatorText.append(String.format("✅ Правильных ответов: %d/%d%n", result.getScore(), result.getTotalQuestions()));
        creatorText.append(String.format("📈 Процент правильных: %.1f%%%n%n", result.getPercentage()));
        creatorText.append("Посмотреть полный рейтинг друзей можно в главном меню! 🏆");

        return new BotResponse(new SendMessage(test.getCreatorId().toString(), creatorText.toString()));
    }

    public BotResponse createCancelResponse(Long chatId) {
        BotResponse cancelResponse = new BotResponse(new SendMessage(chatId.toString(), "❌ Действие отменено."));
        BotResponse welcomeResponse = createWelcomeResponse(chatId, "друг");
        return new BotResponse(cancelResponse, welcomeResponse);
    }

    public BotResponse createTestNotFoundResponse(Long chatId) {
        return new BotResponse(new SendMessage(chatId.toString(), "❌ Тест не найден! Возможно, ссылка устарела или неверна."));
    }

    public BotResponse createErrorResponse(Long chatId, String error) {
        return new BotResponse(new SendMessage(chatId.toString(), String.format("❌ Ошибка: %s", error)));
    }

    public BotResponse createDefaultResponse(Long chatId) {
        return new BotResponse(new SendMessage(chatId.toString(), "Используй кнопки для навигации или /start для главного меню"));
    }

    public BotResponse createFriendsRankingResponse(Long chatId, FriendshipTest test, List<Map.Entry<Long, TestResult>> ranking) {
        if (ranking.isEmpty()) {
            return createNoFriendsResultsResponse(chatId);
        }

        StringBuilder text = new StringBuilder();
        text.append(String.format("🏆 Рейтинг друзей для теста '%s'%n%n", test.getCreatorName()));

        int position = 1;
        for (Map.Entry<Long, TestResult> entry : ranking) {
            TestResult result = entry.getValue();
            String friendName = test.getFriendName(entry.getKey());

            text.append(String.format("%s %s%n   ⭐ %d/%d (%.1f%%)%n%n",
                    getPositionEmoji(position),
                    friendName,
                    result.getScore(),
                    result.getTotalQuestions(),
                    result.getPercentage()
            ));

            position++;
            if (position > 10) break;
        }

        text.append(String.format("Всего прошло тест: %d друзей", ranking.size()));

        SendMessage message = new SendMessage(chatId.toString(), text.toString());
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());
        return new BotResponse(message);
    }

    public BotResponse createNoFriendsResultsResponse(Long chatId) {
        StringBuilder text = new StringBuilder();
        text.append("📊 Пока никто не прошел ваш тест!%n%n");
        text.append("Отправьте ссылку на тест друзьям, чтобы увидеть их результаты здесь.");

        SendMessage message = new SendMessage(chatId.toString(), String.format(text.toString()));
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());
        return new BotResponse(message);
    }

    public BotResponse createNoTestsResponse(Long chatId) {
        StringBuilder text = new StringBuilder();
        text.append("📝 У вас еще нет созданных тестов!%n%n");
        text.append("Создайте тест, чтобы увидеть рейтинг друзей.");

        SendMessage message = new SendMessage(chatId.toString(), String.format(text.toString()));
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());
        return new BotResponse(message);
    }

    private String getPositionEmoji(int position) {
        switch (position) {
            case 1:
                return "🥇";
            case 2:
                return "🥈";
            case 3:
                return "🥉";
            default:
                return "🔸";
        }
    }
}