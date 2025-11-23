package org.example.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.util.ArrayList;
import java.util.List;

public class BotResponse {
    private final List<PartialBotApiMethod> messages;

    public BotResponse(SendMessage message) {
        this.messages = new ArrayList<>();
        if (message != null) {
            this.messages.add(message);
        }
    }

    public BotResponse(SendPhoto photoMessage) {
        this.messages = new ArrayList<>();
        if (photoMessage != null) {
            this.messages.add(photoMessage);
        }
    }

    public BotResponse(BotResponse... responses) {
        this.messages = new ArrayList<>();
        for (BotResponse response : responses) {
            if (response != null) {
                this.messages.addAll(response.getMessages());
            }
        }
    }

    public List<PartialBotApiMethod> getMessages() {
        return messages;
    }

    public boolean hasMessages() {
        return !messages.isEmpty();
    }
}