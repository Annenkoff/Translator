package me.annenkov.translator.manager;

import android.util.ArrayMap;

import java.util.Map;

/**
 * Класс для работы с кэшем.
 * Весь кэш хранится исключительно в RAM, чтобы
 * не оставлять следов в памяти устройства.
 * <p>
 * Основная работа производится с объектом cache типа Map.
 * Ключ объекта cache - сокращение "первого" языка.
 * Значение объекта cache - ещё объект типа Map,
 * где ключ - язык для перевода, а значение - переведённый текст операции.
 */
public class CacheManager {
    private static Map<String, Map<String, String>> cache = new ArrayMap<>();

    private static void checkLanguageCache(String languageReduction) {
        if (cache.get(languageReduction) == null)
            cache.put(languageReduction, new ArrayMap<String, String>());
    }

    public static void addToCache(String languageReduction, String firstText, String secondText) {
        checkLanguageCache(languageReduction);
        cache.get(languageReduction).put(firstText, secondText);
    }

    public static String getTranslationFromText(String languageReduction, String firstText) {
        checkLanguageCache(languageReduction);
        return cache.get(languageReduction).get(firstText);
    }
}
