package org.example.bot;

import org.example.service.TestManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class FriendshipTestBot extends TelegramLongPollingBot {
    private final TestManager testManager;
    private final MessageHandler messageHandler;

    public FriendshipTestBot() {
        this.testManager = new TestManager();
        this.messageHandler = new MessageHandler(testManager);
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

    @Override
    public void onUpdateReceived(Update update) {
        try {
            BotResponse response = messageHandler.handleMessage(update);
            if (response != null && response.hasMessages()) {
                for (SendMessage message : response.getMessages()) {
                    executeMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}