package org.example.bot;

import org.example.service.TestManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FriendshipTestBot extends TelegramLongPollingBot {
    private final TestManager testManager;
    private final MessageHandler messageHandler;
    private final Properties properties;

    public FriendshipTestBot() {
        this.properties = new Properties();
        loadProperties();
        this.testManager = new TestManager();
        this.messageHandler = new MessageHandler(testManager);
    }

    private void loadProperties() {
        try {
            FileInputStream envFile = new FileInputStream(".env");
            properties.load(envFile);
            envFile.close();
        } catch (IOException e) {
            // Файл .env не найден, используем системные переменные
        }
    }

    @Override
    public String getBotUsername() {
        String envUsername = properties.getProperty("BOT_USERNAME");
        if (envUsername != null && !envUsername.isEmpty()) {
            return envUsername;
        }

        String systemUsername = System.getenv("BOT_USERNAME");
        if (systemUsername != null && !systemUsername.isEmpty()) {
            return systemUsername;
        }

        return "Test_On_Friends_bot";
    }

    @Override
    public String getBotToken() {
        String envToken = properties.getProperty("BOT_TOKEN");
        if (envToken != null && !envToken.isEmpty()) {
            return envToken;
        }

        String systemToken = System.getenv("BOT_TOKEN");
        if (systemToken != null && !systemToken.isEmpty()) {
            return systemToken;
        }

        throw new IllegalStateException("BOT_TOKEN не установлен");
    }

    private void executeMessage(PartialBotApiMethod message) {
        try {
            if (message instanceof SendMessage) {
                execute((SendMessage) message);
            } else if (message instanceof SendPhoto) {
                execute((SendPhoto) message);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            BotResponse response = messageHandler.handleMessage(update);
            if (response != null && response.hasMessages()) {
                for (PartialBotApiMethod message : response.getMessages()) {
                    executeMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}