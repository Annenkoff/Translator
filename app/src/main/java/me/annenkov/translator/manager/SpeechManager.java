package me.annenkov.translator.manager;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import ru.yandex.speechkit.SpeechKit;
import ru.yandex.speechkit.Vocalizer;

/**
 * Класс для работы с YandexSpeechApi.
 */
public class SpeechManager {
    private static final String YANDEX_API_KEY = "765dfb27-db30-4f69-85e4-972c794a039b";

    private static Vocalizer mVocalizer;

    public static void init(Context context) {
        SpeechKit.getInstance().configure(context, YANDEX_API_KEY);
    }

    public static void vocalizeText(Context context, String language, String text) {
        if (TextUtils.isEmpty(language)) {
            Toast.makeText(context, "Этот язык пока нельзя озвучить.", Toast.LENGTH_SHORT).show();
        } else if (text.length() > 100) {
            Toast.makeText(context, "Текст слишком длинный для озвучки.", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isEmpty(text)) {
            resetVocalizer();
            Vocalizer vocalizer = Vocalizer.createVocalizer(language, text, true);
            vocalizer.start();
        }
    }

    private static void resetVocalizer() {
        if (mVocalizer != null) {
            mVocalizer.cancel();
            mVocalizer = null;
        }
    }
}
