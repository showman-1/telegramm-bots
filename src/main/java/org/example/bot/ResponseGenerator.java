package org.example.bot;

import org.example.model.FriendshipTest;
import org.example.model.Question;
import org.example.model.TestResult;
import org.example.service.TestManager;
import org.example.util.KeyboardHelper;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

public class ResponseGenerator {

    public BotResponse createWelcomeResponse(Long chatId, String userName) {
        String text = "👋 Привет, " + userName + "!\n\n" +
                "Добро пожаловать в бот 'Тест на дружбу'! 🎯\n\n" +
                "Здесь ты можешь:\n" +
                "• 📝 Создать свой тест с 15 вопросами о себе\n" +
                "• 🔗 Получить ссылку для друзей\n" +
                "• 🎯 Узнать, насколько хорошо друзья тебя знают\n\n" +
                "Выбери действие:";

        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());
        return new BotResponse(message);
    }

    public BotResponse createHelpResponse(Long chatId) {
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

        return new BotResponse(new SendMessage(chatId.toString(), text));
    }

    public BotResponse createTestCreationStartResponse(Long chatId) {
        String text = "🎉 Отлично! Ты начал создание теста на дружбу!\n\n" +
                "Я буду задавать тебе 15 вопросов о себе. " +
                "Выбирай те варианты ответов, которые больше всего тебе подходят.\n\n" +
                "Давай начнем! ✨";

        return new BotResponse(new SendMessage(chatId.toString(), text));
    }

    public BotResponse createTestTakingStartResponse(Long chatId, String creatorName) {
        String text = "🎯 Ты начал тест на дружбу от " + creatorName + "!\n\n" +
                "Отвечай на вопросы так, как думаешь, что ответил бы твой друг.\n\n" +
                "Удачи! 🍀";

        return new BotResponse(new SendMessage(chatId.toString(), text));
    }

    public BotResponse createQuestionResponse(Long chatId, Long userId, TestManager testManager) {
        var question = testManager.getNextQuestion(userId);
        if (question == null) {
            return null;
        }

        var session = testManager.getUserSession(userId);
        int questionNumber = session.getCurrentQuestionIndex() + 1;
        int totalQuestions = 15;

        String caption = "❓ Вопрос " + questionNumber + "/" + totalQuestions + ":\n" + question.getText();

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
        String text = "🎉 Поздравляю! Ты создал тест на дружбу!\n\n" +
                "Теперь отправь эту ссылку друзьям:\n\n" +
                "🔗 " + testUrl + "\n\n" +
                "Когда друзья пройдут твой тест, ты увидишь результаты! 📊";

        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(KeyboardHelper.createMainMenuKeyboard());
        return new BotResponse(message);
    }

    public BotResponse createTestResultResponse(Long chatId, TestResult result, String creatorName) {
        String text = "📊 Результаты теста от " + creatorName + ":\n\n" +
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
        return new BotResponse(message);
    }

    public BotResponse createCreatorNotificationResponse(FriendshipTest test, Long userId, TestResult result) {
        String creatorText = "📊 Кто-то прошел ваш тест!\n\n" +
                "✅ Правильных ответов: " + result.getScore() + "/" + result.getTotalQuestions() + "\n" +
                "📈 Процент правильных: " + String.format("%.1f", result.getPercentage()) + "%";

        return new BotResponse(new SendMessage(test.getCreatorId().toString(), creatorText));
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
        return new BotResponse(new SendMessage(chatId.toString(), "❌ Ошибка: " + error));
    }

    public BotResponse createDefaultResponse(Long chatId) {
        return new BotResponse(new SendMessage(chatId.toString(), "Используй кнопки для навигации или /start для главного меню"));
    }
}