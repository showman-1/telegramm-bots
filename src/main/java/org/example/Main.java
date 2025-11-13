package org.example;

import org.example.bot.FriendshipTestBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Главный класс приложения.
 * Запускает бота и регистрирует его в Telegram API.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Запуск приложения Friendship Test Bot...");

        try {
            // Создаем API для работы с ботами
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Создаем и регистрируем нашего бота
            FriendshipTestBot bot = new FriendshipTestBot();
            botsApi.registerBot(bot);

            logger.info("✅ Бот успешно запущен и зарегистрирован!");
            logger.info("🤖 Имя бота: {}", bot.getBotUsername());
            logger.info("🚀 Бот готов к работе!");

        } catch (TelegramApiException e) {
            logger.error("❌ Ошибка при запуске бота", e);
            System.exit(1);
        }
    }
}