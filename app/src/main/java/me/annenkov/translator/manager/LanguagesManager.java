package me.annenkov.translator.manager;

import android.content.Context;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.annenkov.translator.R;
import me.annenkov.translator.activity.MainActivity;
import ru.yandex.speechkit.Vocalizer;

/**
 * Класс для работы с языками.
 * Хранит в себе два текущих языка - "первый" и "второй".
 * Понятия "первый" и "второй" используются для упрощения.
 * "первый" - язык, с которого требуется перевести.
 * "второй" - язык, на который требуется перевести.
 */
public class LanguagesManager {
    private static MainActivity sMainActivity;
    private static Map<String, String> mLanguageReductions;

    public static void init(MainActivity activity) {
        sMainActivity = activity;

        activity.getFirstLanguageButton().setText(activity.getResources().getString(R.string.russian));
        activity.getSecondLanguageButton().setText(activity.getResources().getString(R.string.english));

        mLanguageReductions = new ArrayMap<>();
        mLanguageReductions.put(activity.getResources().getString(R.string.russian), "ru");
        mLanguageReductions.put(activity.getResources().getString(R.string.english), "en");
        mLanguageReductions.put(activity.getResources().getString(R.string.polish), "pl");
        mLanguageReductions.put(activity.getResources().getString(R.string.italian), "it");
        mLanguageReductions.put(activity.getResources().getString(R.string.german), "de");
        mLanguageReductions.put(activity.getResources().getString(R.string.portuguese), "pt");
        mLanguageReductions.put(activity.getResources().getString(R.string.norwegian), "no");
        mLanguageReductions.put(activity.getResources().getString(R.string.ukrainian), "uk");
        mLanguageReductions.put(activity.getResources().getString(R.string.greek), "el");
        mLanguageReductions.put(activity.getResources().getString(R.string.chinese), "zh");
        mLanguageReductions.put(activity.getResources().getString(R.string.japanese), "ja");
        mLanguageReductions.put(activity.getResources().getString(R.string.turkish), "tr");
        mLanguageReductions.put(activity.getResources().getString(R.string.indonesian), "id");
        mLanguageReductions.put(activity.getResources().getString(R.string.hebrew), "he");
        mLanguageReductions.put(activity.getResources().getString(R.string.latin), "la");
        mLanguageReductions.put(activity.getResources().getString(R.string.lithuanian), "lt");
    }

    public static String getFirstLanguage() {
        return sMainActivity.getFirstLanguageButton().getText().toString();
    }

    public static void setFirstLanguage(String firstLanguage) {
        if (getSecondLanguage().equals(firstLanguage))
            sMainActivity.getSecondLanguageButton().setText(getFirstLanguage());
        sMainActivity.getFirstLanguageButton().setText(firstLanguage);
    }

    public static String getFirstLanguageReduction() {
        return getLanguageReduction(getFirstLanguage());
    }

    public static String getSecondLanguage() {
        return sMainActivity.getSecondLanguageButton().getText().toString();
    }

    public static void setSecondLanguage(String secondLanguage) {
        if (getFirstLanguage().equals(secondLanguage))
            sMainActivity.getFirstLanguageButton().setText(getSecondLanguage());
        sMainActivity.getSecondLanguageButton().setText(secondLanguage);
    }

    public static String getSecondLanguageReduction() {
        return getLanguageReduction(getSecondLanguage());
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

    public static void swapLanguages() {
        String buffer = getFirstLanguage();
        sMainActivity.getFirstLanguageButton().setText(getSecondLanguage());
        sMainActivity.getSecondLanguageButton().setText(buffer);
    }

    public static String getVocalizerLanguage(Context context, String text) {
        String languageReduction = getRightLanguageReduction(context, text);
        if (languageReduction.equals("en")) return Vocalizer.Language.ENGLISH;
        if (languageReduction.equals("ru")) return Vocalizer.Language.RUSSIAN;
        if (languageReduction.equals("uk")) return Vocalizer.Language.UKRAINIAN;
        if (languageReduction.equals("tr")) return Vocalizer.Language.TURKISH;
        else return "";
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
