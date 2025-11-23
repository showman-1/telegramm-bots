package org.example.model;

import java.util.List;

public class Question {
    private String text;
    private String correctAnswer;
    private List<String> options;
    private String imagePath;

    public Question() {
    }

    public Question(String text, List<String> options, String imagePath) {
        this.text = text;
        this.options = options;
        this.imagePath = imagePath;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getOptions() {
        return options;
    }
    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}