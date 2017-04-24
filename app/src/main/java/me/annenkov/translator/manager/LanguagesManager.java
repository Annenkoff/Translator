package me.annenkov.translator.manager;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.annenkov.translator.Extras;
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
    private static boolean sIsFirstLanguageRight;
    private static Map<String, String> sLanguageReductions;

    public static void init(MainActivity mainActivity) {
        sLanguageReductions = new ArrayMap<>();
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.russian), "ru");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.english), "en");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.polish), "pl");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.italian), "it");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.german), "de");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.portuguese), "pt");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.norwegian), "no");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.ukrainian), "uk");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.greek), "el");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.chinese), "zh");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.japanese), "ja");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.turkish), "tr");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.indonesian), "id");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.hebrew), "he");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.latin), "la");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.lithuanian), "lt");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.azerbaijani), "az");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.albanian), "sq");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.amharic), "am");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.arab), "ar");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.armenian), "hy");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.african), "af");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.basque), "eu");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.bashkir), "ba");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.belorussian), "be");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.bengal), "bn");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.bulgarian), "bg");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.bosnian), "bs");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.welsh), "cy");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.hungarian), "hu");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.vietnamese), "vi");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.haitian), "ht");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.galician), "gl");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.mining), "mrj");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.georgian), "ka");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.gujarati), "gu");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.danish), "da");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.yiddish), "yi");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.irish), "ga");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.icelandic), "is");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.spanish), "es");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.kazakh), "kk");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.kannada), "kn");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.catalan), "ca");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.kyrgyz), "ky");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.korean), "ko");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.luxembourgish), "lb");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.malagasy), "mg");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.malay), "ms");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.malayalam), "ml");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.maltese), "mt");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.maori), "mi");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.marathi), "mr");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.mari), "mhr");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.mongolian), "mn");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.nepali), "ne");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.punjabi), "pa");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.papiamento), "pap");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.persian), "fa");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.romanian), "ro");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.sebuanian), "ceb");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.serbian), "sr");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.sinhalese), "si");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.slovak), "sk");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.slovenian), "sl");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.swahili), "sw");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.sudanese), "su");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.tajik), "tg");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.thai), "th");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.tagalog), "tl");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.tatar), "tt");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.uzbek), "uz");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.finnish), "fi");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.hindi), "hi");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.croatian), "hr");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.swedish), "sv");
        sLanguageReductions.put(mainActivity.getResources().getString(R.string.javanese), "jv");

        // Проверяем наличие сохранённых языков. Если есть, устанавливаем.
        if (mainActivity.getSharedPreferences().contains(Extras.PREFERENCE_FIRST_LANGUAGE)
                && mainActivity.getSharedPreferences().contains(Extras.PREFERENCE_SECOND_LANGUAGE)) {
            LanguagesManager.setFirstLanguage(mainActivity, LanguagesManager
                    .getLanguage(mainActivity.getSharedPreferences()
                            .getString(Extras.PREFERENCE_FIRST_LANGUAGE, "")));
            LanguagesManager.setSecondLanguage(mainActivity, LanguagesManager
                    .getLanguage(mainActivity.getSharedPreferences()
                            .getString(Extras.PREFERENCE_SECOND_LANGUAGE, "")));
        }

        // Если кнопки с языками всё ещё пустые, заполняем стандартными значениями.
        if (getFirstLanguage(mainActivity).isEmpty())
            mainActivity.getFirstLanguageButton().setText(mainActivity.getResources().getString(R.string.russian));
        if (getSecondLanguage(mainActivity).isEmpty())
            mainActivity.getSecondLanguageButton().setText(mainActivity.getResources().getString(R.string.english));
    }

    public static boolean isFirstLanguageRight() {
        return sIsFirstLanguageRight;
    }

    public static void setIsFirstLanguageRight(boolean isFirstLanguageRight) {
        LanguagesManager.sIsFirstLanguageRight = isFirstLanguageRight;
    }

    private static String getFirstLanguage(MainActivity mainActivity) {
        return mainActivity.getFirstLanguageButton().getText().toString();
    }

    public static void setFirstLanguage(MainActivity mainActivity, String firstLanguage) {
        if (getSecondLanguage(mainActivity).equals(firstLanguage))
            mainActivity.getSecondLanguageButton().setText(getFirstLanguage(mainActivity));
        mainActivity.getFirstLanguageButton().setText(firstLanguage);
    }

    public static String getFirstLanguageReduction(MainActivity mainActivity) {
        return getLanguageReduction(getFirstLanguage(mainActivity));
    }

    private static String getSecondLanguage(MainActivity mainActivity) {
        return mainActivity.getSecondLanguageButton().getText().toString();
    }

    public static void setSecondLanguage(MainActivity mainActivity, String secondLanguage) {
        if (getFirstLanguage(mainActivity).equals(secondLanguage))
            mainActivity.getFirstLanguageButton().setText(getSecondLanguage(mainActivity));
        mainActivity.getSecondLanguageButton().setText(secondLanguage);
    }

    public static String getSecondLanguageReduction(MainActivity mainActivity) {
        return getLanguageReduction(getSecondLanguage(mainActivity));
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
        String result = sLanguageReductions.get(language);
        if (result == null) return "";
        return result;
    }

    public static void swapLanguages(MainActivity mainActivity) {
        String buffer = getFirstLanguage(mainActivity);
        mainActivity.getFirstLanguageButton().setText(getSecondLanguage(mainActivity));
        mainActivity.getSecondLanguageButton().setText(buffer);
    }

    public static String getVocalizerLanguage(String language) {
        if (language.equals("en")) return Vocalizer.Language.ENGLISH;
        if (language.equals("ru")) return Vocalizer.Language.RUSSIAN;
        if (language.equals("uk")) return Vocalizer.Language.UKRAINIAN;
        if (language.equals("tr")) return Vocalizer.Language.TURKISH;
        else return "";
    }

    public static List<String> getLanguagesList() {
        List<String> languages = new ArrayList<>();
        languages.addAll(sLanguageReductions.keySet());
        Collections.sort(languages);
        return languages;
    }
}
