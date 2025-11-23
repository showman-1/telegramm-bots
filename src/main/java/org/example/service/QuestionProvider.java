package org.example.service;

import org.example.model.Question;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionProvider {
    private static final List<Question> DEFAULT_QUESTIONS = createDefaultQuestions();

    private static List<Question> createDefaultQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question("Сколько мне лет?", Arrays.asList("1-10", "11-15", "16-20", "21-99"), "src/main/resources/images/age.jpg"));
        questions.add(new Question("Какую одежду я ношу?", Arrays.asList("Белую", "Темную", "Цветную", "Гоняю без нее"), "src/main/resources/images/clothes.jpg"));
        questions.add(new Question("Важно ли мне мнение окружающих?", Arrays.asList("Да", "Нет", "50/50", "Смотря чье"), "src/main/resources/images/opinion.jpg"));
        questions.add(new Question("Сколько языков я знаю?", Arrays.asList("1", "2", "3", "Больше 3"), "src/main/resources/images/languages.jpg"));
        questions.add(new Question("Какой мой любимый цвет?", Arrays.asList("Белый", "Черный", "Зеленый", "Другие"), "src/main/resources/images/color.jpg"));
        questions.add(new Question("Есть ли у меня братья или сестры?", Arrays.asList("Брат/Братья", "Сестра/Сестры", "Брат и сестра", "Нет"), "src/main/resources/images/siblings.jpg"));
        questions.add(new Question("Какое мое любимое время дня?", Arrays.asList("Утро", "День", "Вечер", "Ночь"), "src/main/resources/images/time.jpg"));
        questions.add(new Question("Часто ли я матерюсь?", Arrays.asList("Да", "Нет", "50/50", "Зависит от ситуации"), "src/main/resources/images/swear.jpg"));
        questions.add(new Question("Чего из перечисленного я никогда не боялся?", Arrays.asList("Насекомых", "Высоты", "Темноты", "Другое"), "src/main/resources/images/fear.jpg"));
        questions.add(new Question("Какие волосы мне нравятся?", Arrays.asList("Кудрявые", "Прямые", "Без разницы", "Без волос"), "src/main/resources/images/hair.jpg"));
        questions.add(new Question("Без чего я бы не смог прожить?", Arrays.asList("Телефона", "Сладостей", "Денег", "Ничего из перечисленного"), "src/main/resources/images/essential.jpg"));
        questions.add(new Question("С кем я предпочитаю гулять?", Arrays.asList("Друзья", "Семья", "Партнер", "Один"), "src/main/resources/images/walk.jpg"));
        questions.add(new Question("Какое мое любимое время года?", Arrays.asList("Лето", "Зима", "Весна", "Осень"), "src/main/resources/images/season.jpg"));
        questions.add(new Question("Если заиграет песня, что я буду делать?", Arrays.asList("Танцевать", "Петь", "Ничего", "Смотря на ситуцию"), "src/main/resources/images/music.jpg"));
        questions.add(new Question("Какой отдых я предпочитаю?", Arrays.asList("Горы", "Море", "Сидеть дома", "Другое"), "src/main/resources/images/vacation.jpg"));

        return questions;
    }

    public static List<Question> getDefaultQuestions() {
        List<Question> questionsCopy = new ArrayList<>();
        for (Question question : DEFAULT_QUESTIONS) {
            questionsCopy.add(new Question(question.getText(), new ArrayList<>(question.getOptions()), question.getImagePath()));
        }
        return questionsCopy;
    }

    public static int getTotalQuestions() {
        return DEFAULT_QUESTIONS.size();
    }
}