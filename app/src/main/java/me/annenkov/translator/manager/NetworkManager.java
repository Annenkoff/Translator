package me.annenkov.translator.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Класс для работы с сетью и API.
 */
public class NetworkManager {
    private static final String YANDEX_API_KEY = "trnsl.1.1.20170317T155546Z.e419594abd6d2bd3.da7c18ede5fa233864ef799143b796f59e910c29";
    private Context mContext;
    private String mText;
    private String mFirstLanguage;
    private String mSecondLanguage;

    public NetworkManager(Context context, String text) {
        mContext = context;
        mText = text;
    }

    public NetworkManager(Context context, String text, String firstLanguage, String secondLanguage) {
        mContext = context;
        mText = text;
        mFirstLanguage = firstLanguage;
        mSecondLanguage = secondLanguage;
    }

    public String getTranslatedText() {
        try {
            return new AsyncRequestToGetTranslatedText().execute(YANDEX_API_KEY,
                    mText,
                    mFirstLanguage,
                    mSecondLanguage).get();
        } catch (InterruptedException | ExecutionException e) {
            return "";
        }
    }

    /**
     * Получение "правильного" сокращения языка.
     * <p>
     * Помогает работать системе по рекомендации языка.
     */
    public String getRightLanguageReduction() {
        try {
            return new AsyncRequestToGetRightLanguageReduction().execute(YANDEX_API_KEY, mText).get();
        } catch (InterruptedException | ExecutionException e) {
            return "";
        }
    }

    private class AsyncRequestToGetTranslatedText extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            try {
                String sURL = String.format("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                        "key=%s" +
                        "&text=%s" +
                        "&lang=%s-%s", arg[0], arg[1], arg[2], arg[3]);
                URL url = new URL(sURL);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();
                JsonParser jsonParser = new JsonParser();
                JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject jsonObject = root.getAsJsonObject();
                return jsonObject.get("text").getAsString();
            } catch (IOException e) {
                return "";
            }
        }
    }

    private class AsyncRequestToGetRightLanguageReduction extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            try {
                String sURL = String.format("https://translate.yandex.net/api/v1.5/tr.json/detect?" +
                        "key=%s" +
                        "&text=%s", arg[0], arg[1]);
                URL url = new URL(sURL);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();
                JsonParser jsonParser = new JsonParser();
                JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject jsonObject = root.getAsJsonObject();
                return jsonObject.get("lang").getAsString();
            } catch (IOException e) {
                return "";
            }
        }
    }
}
