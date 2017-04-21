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
    private static boolean sIsFirstLanguageRight;
    private static Map<String, String> sLanguageReductions;

    public static void init(MainActivity activity) {
        sMainActivity = activity;

        if (getFirstLanguage().isEmpty())
            activity.getFirstLanguageButton().setText(activity.getResources().getString(R.string.russian));
        if (getSecondLanguage().isEmpty())
            activity.getSecondLanguageButton().setText(activity.getResources().getString(R.string.english));

        sLanguageReductions = new ArrayMap<>();
        sLanguageReductions.put(activity.getResources().getString(R.string.russian), "ru");
        sLanguageReductions.put(activity.getResources().getString(R.string.english), "en");
        sLanguageReductions.put(activity.getResources().getString(R.string.polish), "pl");
        sLanguageReductions.put(activity.getResources().getString(R.string.italian), "it");
        sLanguageReductions.put(activity.getResources().getString(R.string.german), "de");
        sLanguageReductions.put(activity.getResources().getString(R.string.portuguese), "pt");
        sLanguageReductions.put(activity.getResources().getString(R.string.norwegian), "no");
        sLanguageReductions.put(activity.getResources().getString(R.string.ukrainian), "uk");
        sLanguageReductions.put(activity.getResources().getString(R.string.greek), "el");
        sLanguageReductions.put(activity.getResources().getString(R.string.chinese), "zh");
        sLanguageReductions.put(activity.getResources().getString(R.string.japanese), "ja");
        sLanguageReductions.put(activity.getResources().getString(R.string.turkish), "tr");
        sLanguageReductions.put(activity.getResources().getString(R.string.indonesian), "id");
        sLanguageReductions.put(activity.getResources().getString(R.string.hebrew), "he");
        sLanguageReductions.put(activity.getResources().getString(R.string.latin), "la");
        sLanguageReductions.put(activity.getResources().getString(R.string.lithuanian), "lt");
        sLanguageReductions.put(activity.getResources().getString(R.string.azerbaijani), "az");
        sLanguageReductions.put(activity.getResources().getString(R.string.albanian), "sq");
        sLanguageReductions.put(activity.getResources().getString(R.string.amharic), "am");
        sLanguageReductions.put(activity.getResources().getString(R.string.arab), "ar");
        sLanguageReductions.put(activity.getResources().getString(R.string.armenian), "hy");
        sLanguageReductions.put(activity.getResources().getString(R.string.african), "af");
        sLanguageReductions.put(activity.getResources().getString(R.string.basque), "eu");
        sLanguageReductions.put(activity.getResources().getString(R.string.bashkir), "ba");
        sLanguageReductions.put(activity.getResources().getString(R.string.belorussian), "be");
        sLanguageReductions.put(activity.getResources().getString(R.string.bengal), "bn");
        sLanguageReductions.put(activity.getResources().getString(R.string.bulgarian), "bg");
        sLanguageReductions.put(activity.getResources().getString(R.string.bosnian), "bs");
        sLanguageReductions.put(activity.getResources().getString(R.string.welsh), "cy");
        sLanguageReductions.put(activity.getResources().getString(R.string.hungarian), "hu");
        sLanguageReductions.put(activity.getResources().getString(R.string.vietnamese), "vi");
        sLanguageReductions.put(activity.getResources().getString(R.string.haitian), "ht");
        sLanguageReductions.put(activity.getResources().getString(R.string.galician), "gl");
        sLanguageReductions.put(activity.getResources().getString(R.string.mining), "mrj");
        sLanguageReductions.put(activity.getResources().getString(R.string.georgian), "ka");
        sLanguageReductions.put(activity.getResources().getString(R.string.gujarati), "gu");
        sLanguageReductions.put(activity.getResources().getString(R.string.danish), "da");
        sLanguageReductions.put(activity.getResources().getString(R.string.yiddish), "yi");
        sLanguageReductions.put(activity.getResources().getString(R.string.irish), "ga");
        sLanguageReductions.put(activity.getResources().getString(R.string.icelandic), "is");
        sLanguageReductions.put(activity.getResources().getString(R.string.spanish), "es");
        sLanguageReductions.put(activity.getResources().getString(R.string.kazakh), "kk");
        sLanguageReductions.put(activity.getResources().getString(R.string.kannada), "kn");
        sLanguageReductions.put(activity.getResources().getString(R.string.catalan), "ca");
        sLanguageReductions.put(activity.getResources().getString(R.string.kyrgyz), "ky");
        sLanguageReductions.put(activity.getResources().getString(R.string.korean), "ko");
        sLanguageReductions.put(activity.getResources().getString(R.string.luxembourgish), "lb");
        sLanguageReductions.put(activity.getResources().getString(R.string.malagasy), "mg");
        sLanguageReductions.put(activity.getResources().getString(R.string.malay), "ms");
        sLanguageReductions.put(activity.getResources().getString(R.string.malayalam), "ml");
        sLanguageReductions.put(activity.getResources().getString(R.string.maltese), "mt");
        sLanguageReductions.put(activity.getResources().getString(R.string.maori), "mi");
        sLanguageReductions.put(activity.getResources().getString(R.string.marathi), "mr");
        sLanguageReductions.put(activity.getResources().getString(R.string.mari), "mhr");
        sLanguageReductions.put(activity.getResources().getString(R.string.mongolian), "mn");
        sLanguageReductions.put(activity.getResources().getString(R.string.nepali), "ne");
        sLanguageReductions.put(activity.getResources().getString(R.string.punjabi), "pa");
        sLanguageReductions.put(activity.getResources().getString(R.string.papiamento), "pap");
        sLanguageReductions.put(activity.getResources().getString(R.string.persian), "fa");
        sLanguageReductions.put(activity.getResources().getString(R.string.romanian), "ro");
        sLanguageReductions.put(activity.getResources().getString(R.string.sebuanian), "ceb");
        sLanguageReductions.put(activity.getResources().getString(R.string.serbian), "sr");
        sLanguageReductions.put(activity.getResources().getString(R.string.sinhalese), "si");
        sLanguageReductions.put(activity.getResources().getString(R.string.slovak), "sk");
        sLanguageReductions.put(activity.getResources().getString(R.string.slovenian), "sl");
        sLanguageReductions.put(activity.getResources().getString(R.string.swahili), "sw");
        sLanguageReductions.put(activity.getResources().getString(R.string.sudanese), "su");
        sLanguageReductions.put(activity.getResources().getString(R.string.tajik), "tg");
        sLanguageReductions.put(activity.getResources().getString(R.string.thai), "th");
        sLanguageReductions.put(activity.getResources().getString(R.string.tagalog), "tl");
        sLanguageReductions.put(activity.getResources().getString(R.string.tatar), "tt");
        sLanguageReductions.put(activity.getResources().getString(R.string.uzbek), "uz");
        sLanguageReductions.put(activity.getResources().getString(R.string.finnish), "fi");
        sLanguageReductions.put(activity.getResources().getString(R.string.hindi), "hi");
        sLanguageReductions.put(activity.getResources().getString(R.string.croatian), "hr");
        sLanguageReductions.put(activity.getResources().getString(R.string.swedish), "sv");
        sLanguageReductions.put(activity.getResources().getString(R.string.javanese), "jv");
    }

    public static boolean isFirstLanguageRight() {
        return sIsFirstLanguageRight;
    }

    public static void setIsFirstLanguageRight(boolean isFirstLanguageRight) {
        LanguagesManager.sIsFirstLanguageRight = isFirstLanguageRight;
    }

    private static String getFirstLanguage() {
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

    public static boolean isLanguageExists(String languageReduction) {
        return sLanguageReductions.containsValue(languageReduction);
    }

    private static String getSecondLanguage() {
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
        for (String s : sLanguageReductions.keySet()) {
            if (sLanguageReductions.get(s).equalsIgnoreCase(languageReduction)) return s;
        }
        return "";
    }

    private static String getLanguageReduction(String language) {
        return sLanguageReductions.get(language);
    }

    public static void swapLanguages() {
        String buffer = getFirstLanguage();
        sMainActivity.getFirstLanguageButton().setText(getSecondLanguage());
        sMainActivity.getSecondLanguageButton().setText(buffer);
    }

    public static String getVocalizerLanguage(Context context, String text, String language) {
        if (language.equals("en")) return Vocalizer.Language.ENGLISH;
        if (language.equals("ru")) return Vocalizer.Language.RUSSIAN;
        if (language.equals("uk")) return Vocalizer.Language.UKRAINIAN;
        if (language.equals("tr")) return Vocalizer.Language.TURKISH;
        else return "";
    }

    public static Map<String, String> getLanguageReductions() {
        return sLanguageReductions;
    }

    public static List<String> getLanguagesList() {
        List<String> languages = new ArrayList<>();
        languages.addAll(sLanguageReductions.keySet());
        Collections.sort(languages);
        return languages;
    }
}
