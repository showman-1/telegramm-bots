package org.example.model;

import java.util.List;

/**
 * Класс представляет один вопрос теста.
 * Содержит текст вопроса, правильный ответ и варианты ответов.
 */
// Конструктор по умолчанию (нужен для создания копий)

public class Question {
    private String text;              // Текст вопроса
    private String correctAnswer;     // Правильный ответ (заполняется создателем теста)
    private List<String> options;     // Варианты ответов
    public Question() {
    }
    // Конструктор
    public Question(String text, List<String> options) {
        this.text = text;
        this.options = options;
    }

    // Геттеры и сеттеры
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}