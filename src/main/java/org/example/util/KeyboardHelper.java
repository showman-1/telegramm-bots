package org.example.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardHelper {
    public static InlineKeyboardMarkup createOptionsKeyboard(List<String> options) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (String option : options) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(option);
            button.setCallbackData("answer_" + option);
            row.add(button);
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public static InlineKeyboardMarkup createMainMenuKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton createButton = new InlineKeyboardButton();
        createButton.setText("Создать Тест");
        createButton.setCallbackData("create_test");
        row1.add(createButton);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton helpButton = new InlineKeyboardButton();
        helpButton.setText("Помощь");
        helpButton.setCallbackData("help");
        row2.add(helpButton);

        rows.add(row1);
        rows.add(row2);
        keyboard.setKeyboard(rows);

        return keyboard;
    }

    public static InlineKeyboardMarkup createCancelKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("Отмена");
        cancelButton.setCallbackData("cancel");
        row.add(cancelButton);
        rows.add(row);
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}