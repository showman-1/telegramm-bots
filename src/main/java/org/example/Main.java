package org.example;

import org.example.bot.FriendshipTestBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            FriendshipTestBot bot = new FriendshipTestBot();
            botsApi.registerBot(bot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}