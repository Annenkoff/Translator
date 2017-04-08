package me.annenkov.translator.manager;

import android.content.Context;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.annenkov.translator.R;

public class LanguagesManager {
    private Context mContext;
    private String mFirstLanguage;
    private String mSecondLanguage;
    private Map<String, String> mLanguageReductions;

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

    public String getFirstLanguage() {
        return mFirstLanguage;
    }

    public void setFirstLanguage(String firstLanguage) {
        mFirstLanguage = firstLanguage;
    }

    public String getSecondLanguage() {
        return mSecondLanguage;
    }

    public void setSecondLanguage(String secondLanguage) {
        mSecondLanguage = secondLanguage;
    }

    public Map<String, String> getLanguageReductions() {
        return mLanguageReductions;
    }

    public List<String> getLanguagesList() {
        List<String> languages = new ArrayList<>();
        languages.addAll(mLanguageReductions.keySet());
        Collections.sort(languages);
        return languages;
    }
}
