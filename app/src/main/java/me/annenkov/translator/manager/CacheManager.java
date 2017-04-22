package me.annenkov.translator.manager;

import android.util.ArrayMap;

import java.util.Map;

/**
 * Класс для работы с кэшем.
 * Весь кэш хранится исключительно в RAM, чтобы
 * не оставлять следов в памяти устройства.
 * <p>
 * Основная работа производится с объектом cache типа Map.
 * Ключ объекта cache - сокращение "второго" языка.
 * Значение объекта cache - тоже объект типа Map,
 * где ключ - не переведённый текст, а значение
 * - переведённый текст операции.
 */
public class CacheManager {
    private static Map<String, Map<String, String>> cache = new ArrayMap<>();

    private static void checkLanguageCache(String secondLanguageReduction) {
        if (cache.get(secondLanguageReduction) == null)
            cache.put(secondLanguageReduction, new ArrayMap<String, String>());
    }

    public static void addToCache(String secondLanguageReduction, String firstText, String secondText) {
        checkLanguageCache(secondLanguageReduction);
        cache.get(secondLanguageReduction).put(firstText, secondText);
    }

    public static String getTranslationFromText(String languageReduction, String firstText) {
        checkLanguageCache(languageReduction);
        return cache.get(languageReduction).get(firstText);
    }
}
