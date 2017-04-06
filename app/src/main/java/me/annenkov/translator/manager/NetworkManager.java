package me.annenkov.translator.manager;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class NetworkManager {
    private String mYandexApiKey;
    private String mText;
    private String mFirstLanguage;
    private String mSecondLanguage;

    public NetworkManager(String yandexApiKey, String text, String firstLanguage, String secondLanguage) {
        mYandexApiKey = yandexApiKey;
        mText = text;
        mFirstLanguage = firstLanguage;
        mSecondLanguage = secondLanguage;
    }

    public String getTranslateText() {
        try {
            return new AsyncRequest().execute(mYandexApiKey,
                    mText,
                    mFirstLanguage,
                    mSecondLanguage).get();
        } catch (InterruptedException | ExecutionException e) {
            return "";
        }
    }

    private class AsyncRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            String doc;
            try {
                doc = Jsoup.connect("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                        "key=" + arg[0] +
                        "&text=" + arg[1] +
                        "&lang=" + arg[2] +
                        "-" + arg[3])
                        .ignoreContentType(true)
                        .execute()
                        .body();
                String text = new JSONObject(doc).getString("text");
                return text.substring(2, text.length() - 2);
            } catch (IOException | JSONException e) {
                return "";
            }
        }
    }
}
