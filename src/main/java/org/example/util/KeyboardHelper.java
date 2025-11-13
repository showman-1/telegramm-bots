package org.example.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилитный класс для создания клавиатур в Telegram.
 * Упрощает создание кнопок и меню.
 */
public class KeyboardHelper {

    /**
     * Создает inline-клавиатуру с вариантами ответов
     */
    public static InlineKeyboardMarkup createOptionsKeyboard(List<String> options) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Создаем кнопки для каждого варианта ответа
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

    /**
     * Создает главное меню бота
     */
    public static InlineKeyboardMarkup createMainMenuKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка "Создать тест"
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton createButton = new InlineKeyboardButton();
        createButton.setText("📝 Создать тест");
        createButton.setCallbackData("create_test");
        row1.add(createButton);

        // Кнопка "Помощь"
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton helpButton = new InlineKeyboardButton();
        helpButton.setText("❓ Помощь");
        helpButton.setCallbackData("help");
        row2.add(helpButton);

        rows.add(row1);
        rows.add(row2);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    /**
     * Создает клавиатуру для отмены действия
     */
    public static InlineKeyboardMarkup createCancelKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("❌ Отменить");
        cancelButton.setCallbackData("cancel");
        row.add(cancelButton);

        rows.add(row);
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}