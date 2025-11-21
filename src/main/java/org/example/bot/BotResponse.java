package org.example.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

public class BotResponse {
    private final List<SendMessage> messages;

    public BotResponse(SendMessage message) {
        this.messages = new ArrayList<>();
        if (message != null) {
            this.messages.add(message);
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

    public List<SendMessage> getMessages() {
        return messages;
    }

    public boolean hasMessages() {
        return !messages.isEmpty();
    }
}