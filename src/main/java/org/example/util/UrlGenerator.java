package org.example.util;

public class UrlGenerator {
    private static final String BOT_USERNAME = "Test_On_Friends_bot";

    public static String generateTestUrl(String testId) {
        return "https://t.me/" + BOT_USERNAME + "?start=" + testId;
    }

    public static String generateTestId() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}