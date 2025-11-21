package org.example.service;

import org.example.model.Question;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionProvider {
    private static final List<Question> DEFAULT_QUESTIONS = createDefaultQuestions();

    private static List<Question> createDefaultQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question("Сколько мне лет?", Arrays.asList("1-10", "11-15", "16-20", "21-99")));
        questions.add(new Question("Какую одежду я ношу?", Arrays.asList("Белую", "Темную", "Цветную", "Гоняю без нее")));
        questions.add(new Question("Важно ли мне мнение окружающих?", Arrays.asList("Да", "Нет", "50/50", "Смотря чье")));
        questions.add(new Question("Сколько языков я знаю?", Arrays.asList("1", "2", "3", "Больше 3")));
        questions.add(new Question("Какой мой любимый цвет?", Arrays.asList("Белый", "Черный", "Зеленый", "Другие")));
        questions.add(new Question("Есть ли у меня братья или сестры?", Arrays.asList("Брат/Братья", "Сестра/Сестры", "Брат и сестра", "Нет")));
        questions.add(new Question("Какое мое любимое время дня?", Arrays.asList("Утро", "День", "Вечер", "Ночь")));
        questions.add(new Question("Часто ли я матерюсь?", Arrays.asList("Да", "Нет", "50/50", "Зависит от ситуации")));
        questions.add(new Question("Чего из перечисленного я никогда не боялся?", Arrays.asList("Насекомых", "Высоты", "Темноты", "Другое")));
        questions.add(new Question("Какие волосы мне нравятся?", Arrays.asList("Кудрявые", "Прямые", "Без разницы", "Без волос")));
        questions.add(new Question("Без чего я бы не смог прожить?", Arrays.asList("Телефона", "Сладостей", "Денег", "Ничего из перечисленного")));
        questions.add(new Question("С кем я предпочитаю гулять?", Arrays.asList("Друзья", "Семья", "Партнер", "Один")));
        questions.add(new Question("Какое мое любимое время года?", Arrays.asList("Лето", "Зима", "Весна", "Осень")));
        questions.add(new Question("Если заиграет песня, что я буду делать?", Arrays.asList("Танцевать", "Петь", "Ничего", "Смотря на ситуцию")));
        questions.add(new Question("Какой отдых я предпочитаю?", Arrays.asList("Горы", "Море", "Сидеть дома", "Другое")));

        return questions;
    }

    public static List<Question> getDefaultQuestions() {
        List<Question> questionsCopy = new ArrayList<>();
        for (Question question : DEFAULT_QUESTIONS) {
            questionsCopy.add(new Question(question.getText(), new ArrayList<>(question.getOptions())));
        }
        return questionsCopy;
    }

    public static int getTotalQuestions() {
        return DEFAULT_QUESTIONS.size();
    }
}