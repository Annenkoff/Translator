package me.annenkov.translator.manager;

import android.content.Context;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.annenkov.translator.R;

public class LanguagesManager {
    private static String mFirstLanguage;
    private static String mSecondLanguage;
    private static Map<String, String> mLanguageReductions;
    private Context mContext;

    public LanguagesManager(Context context) {
        mContext = context;
        mFirstLanguage = mContext.getResources().getString(R.string.russian);
        mSecondLanguage = mContext.getResources().getString(R.string.english);
        mLanguageReductions = new ArrayMap<>();

        mLanguageReductions.put(mContext.getResources().getString(R.string.russian), "ru");
        mLanguageReductions.put(mContext.getResources().getString(R.string.english), "en");
        mLanguageReductions.put(mContext.getResources().getString(R.string.polish), "pl");
        mLanguageReductions.put(mContext.getResources().getString(R.string.italian), "it");
        mLanguageReductions.put(mContext.getResources().getString(R.string.german), "de");
        mLanguageReductions.put(mContext.getResources().getString(R.string.portuguese), "pt");
        mLanguageReductions.put(mContext.getResources().getString(R.string.norwegian), "no");
        mLanguageReductions.put(mContext.getResources().getString(R.string.ukrainian), "uk");
        mLanguageReductions.put(mContext.getResources().getString(R.string.greek), "el");
        mLanguageReductions.put(mContext.getResources().getString(R.string.chinese), "zh");
        mLanguageReductions.put(mContext.getResources().getString(R.string.japanese), "ja");
        mLanguageReductions.put(mContext.getResources().getString(R.string.turkish), "tr");
        mLanguageReductions.put(mContext.getResources().getString(R.string.indonesian), "id");
        mLanguageReductions.put(mContext.getResources().getString(R.string.hebrew), "he");
        mLanguageReductions.put(mContext.getResources().getString(R.string.latin), "la");
        mLanguageReductions.put(mContext.getResources().getString(R.string.lithuanian), "lt");
    }

    public static String getFirstLanguage() {
        return mFirstLanguage;
    }

    public static void setFirstLanguage(String firstLanguage) {
        if (getSecondLanguage().equals(firstLanguage)) mSecondLanguage = mFirstLanguage;
        mFirstLanguage = firstLanguage;
    }

    public static String getLanguageFromLanguageReduction(String languageReduction) {
        for (String s : mLanguageReductions.keySet()) {
            if (mLanguageReductions.get(s).equalsIgnoreCase(languageReduction)) return s;
        }
        return "";
    }

    public static String getSecondLanguage() {
        return mSecondLanguage;
    }

    public static void setSecondLanguage(String secondLanguage) {
        if (getFirstLanguage().equals(secondLanguage)) mFirstLanguage = mSecondLanguage;
        mSecondLanguage = secondLanguage;
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

    public String getRightLanguageReduction(String text) {
        return new NetworkManager(mContext, text).getFirstLanguageReduction();
    }

    public String getRightLanguage(String text) {
        return getLanguageFromLanguageReduction(getRightLanguageReduction(text));
    }

    public boolean isFirstLanguageIsRight(String text) {
        return text.isEmpty() || getRightLanguage(text).equals(getFirstLanguage());
    }

    public void makeFirstLanguageRight(String text) {
        setFirstLanguage(getRightLanguage(text));
    }
}
