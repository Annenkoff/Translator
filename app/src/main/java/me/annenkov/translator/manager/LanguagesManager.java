package me.annenkov.translator.manager;

import android.content.Context;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.annenkov.translator.R;

/**
 * Класс для работы с языками.
 * Хранит в себе два текущих языка - "первый" и "второй".
 * Понятия "первый" и "второй" используются для упрощения.
 * "первый" - язык, с которого требуется перевести.
 * "второй" - язык, на который требуется перевести.
 */
public class LanguagesManager {
    private static String mFirstLanguage;
    private static String mSecondLanguage;
    private static Map<String, String> mLanguageReductions;

    public static void init(Context context) {
        mFirstLanguage = context.getResources().getString(R.string.russian);
        mSecondLanguage = context.getResources().getString(R.string.english);

        mLanguageReductions = new ArrayMap<>();
        mLanguageReductions.put(context.getResources().getString(R.string.russian), "ru");
        mLanguageReductions.put(context.getResources().getString(R.string.english), "en");
        mLanguageReductions.put(context.getResources().getString(R.string.polish), "pl");
        mLanguageReductions.put(context.getResources().getString(R.string.italian), "it");
        mLanguageReductions.put(context.getResources().getString(R.string.german), "de");
        mLanguageReductions.put(context.getResources().getString(R.string.portuguese), "pt");
        mLanguageReductions.put(context.getResources().getString(R.string.norwegian), "no");
        mLanguageReductions.put(context.getResources().getString(R.string.ukrainian), "uk");
        mLanguageReductions.put(context.getResources().getString(R.string.greek), "el");
        mLanguageReductions.put(context.getResources().getString(R.string.chinese), "zh");
        mLanguageReductions.put(context.getResources().getString(R.string.japanese), "ja");
        mLanguageReductions.put(context.getResources().getString(R.string.turkish), "tr");
        mLanguageReductions.put(context.getResources().getString(R.string.indonesian), "id");
        mLanguageReductions.put(context.getResources().getString(R.string.hebrew), "he");
        mLanguageReductions.put(context.getResources().getString(R.string.latin), "la");
        mLanguageReductions.put(context.getResources().getString(R.string.lithuanian), "lt");
    }

    public static String getFirstLanguage() {
        return mFirstLanguage;
    }

    public static void setFirstLanguage(String firstLanguage) {
        if (getSecondLanguage().equals(firstLanguage)) mSecondLanguage = mFirstLanguage;
        mFirstLanguage = firstLanguage;
    }

    public static String getFirstLanguageReduction() {
        return getLanguageReduction(mFirstLanguage);
    }

    public static String getSecondLanguage() {
        return mSecondLanguage;
    }

    public static void setSecondLanguage(String secondLanguage) {
        if (getFirstLanguage().equals(secondLanguage)) mFirstLanguage = mSecondLanguage;
        mSecondLanguage = secondLanguage;
    }

    public static String getSecondLanguageReduction() {
        return getLanguageReduction(mSecondLanguage);
    }

    /**
     * Получение названия языка по его сокращению.
     * Пример: en - english.
     *
     * @param languageReduction Сокращение языка.
     */
    public static String getLanguage(String languageReduction) {
        for (String s : mLanguageReductions.keySet()) {
            if (mLanguageReductions.get(s).equalsIgnoreCase(languageReduction)) return s;
        }
        return "";
    }

    public static String getLanguageReduction(String language) {
        return mLanguageReductions.get(language);
    }

    public static Map<String, String> getLanguageReductions() {
        return mLanguageReductions;
    }

    public static List<String> getLanguagesList() {
        List<String> languages = new ArrayList<>();
        languages.addAll(mLanguageReductions.keySet());
        Collections.sort(languages);
        return languages;
    }

    public static String getRightLanguageReduction(Context context, String text) {
        return new NetworkManager(context, text).getRightLanguageReduction();
    }

    public static String getRightLanguage(Context context, String text) {
        return getLanguage(getRightLanguageReduction(context, text));
    }

    public static boolean isFirstLanguageRight(Context context, String text) {
        return text.isEmpty() || getRightLanguage(context, text).equals(getFirstLanguage());
    }

    public static void makeFirstLanguageRight(Context context, String text) {
        setFirstLanguage(getRightLanguage(context, text));
    }
}
