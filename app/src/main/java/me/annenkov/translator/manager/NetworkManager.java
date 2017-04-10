package me.annenkov.translator.manager;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import me.annenkov.translator.R;

public class NetworkManager {
    private Context mContext;
    private String mYandexApiKey;
    private String mText;
    private String mFirstLanguage;
    private String mSecondLanguage;

    public NetworkManager(Context context, String text, String firstLanguage, String secondLanguage) {
        mContext = context;
        mYandexApiKey = mContext.getString(R.string.yandex_api_key);
        mText = text;
        mFirstLanguage = firstLanguage;
        mSecondLanguage = secondLanguage;
    }

    public String getTranslatedText() {
        try {
            return new AsyncRequestToGetTranslatedText().execute(mYandexApiKey,
                    mText,
                    mFirstLanguage,
                    mSecondLanguage).get();
        } catch (InterruptedException | ExecutionException e) {
            return "";
        }
    }

    private class AsyncRequestToGetTranslatedText extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            String doc;
            try {
                doc = Jsoup.connect(String.format("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                        "key=%s" +
                        "&text=%s" +
                        "&lang=%s-%s", arg[0], arg[1], arg[2], arg[3]))
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
