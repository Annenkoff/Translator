package me.annenkov.translator.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LanguagesManager {
    private String mFirstLanguage;
    private String mSecondLanguage;
    private Map<String, String> mLanguageReductions;

    public LanguagesManager(String firstLanguage, String secondLanguage, Map<String, String> languageReductions) {
        mFirstLanguage = firstLanguage;
        mSecondLanguage = secondLanguage;
        mLanguageReductions = languageReductions;
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
