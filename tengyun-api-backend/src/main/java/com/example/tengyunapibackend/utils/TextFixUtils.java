package com.example.tengyunapibackend.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TextFixUtils {

    private TextFixUtils() {
    }

    public static String fixMojibake(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        if (containsCjk(text)) {
            return text;
        }
        if (!containsPotentialMojibakeChars(text)) {
            return text;
        }
        String isoRecovered = recover(text, StandardCharsets.ISO_8859_1);
        String cp1252Recovered = recover(text, Charset.forName("Windows-1252"));
        int originalScore = cjkScore(text);
        int isoScore = cjkScore(isoRecovered);
        int cpScore = cjkScore(cp1252Recovered);

        if (cpScore > isoScore && cpScore > originalScore) {
            return cp1252Recovered;
        }
        if (isoScore > originalScore) {
            return isoRecovered;
        }
        return text;
    }

    private static String recover(String text, Charset sourceCharset) {
        return new String(text.getBytes(sourceCharset), StandardCharsets.UTF_8);
    }

    private static boolean containsPotentialMojibakeChars(String text) {
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if ((ch >= 0x00C0 && ch <= 0x00FF) || (ch >= 0x0080 && ch <= 0x009F)) {
                return true;
            }
        }
        return false;
    }

    private static int cjkScore(String text) {
        int score = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch >= 0x4E00 && ch <= 0x9FFF) {
                score++;
            }
        }
        return score;
    }

    private static boolean containsCjk(String text) {
        return cjkScore(text) > 0;
    }
}
