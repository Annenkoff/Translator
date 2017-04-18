package me.annenkov.translator.manager;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Класс для работы с сетью и API.
 */
public class NetworkManager {
    private static final String YANDEX_API_KEY = "trnsl.1.1.20170317T155546Z" +
            ".e419594abd6d2bd3" +
            ".da7c18ede5fa233864ef799143b796f59e910c29";

    public static class AsyncRequestToGetTranslatedText extends AsyncTask<String, Integer, String> {
        public AsyncResponse delegate = null;

        public AsyncRequestToGetTranslatedText(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(String... arg) {
            try {
                String sURL =
                        String.format("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                                "key=%s" +
                                "&text=%s" +
                                "&lang=%s-%s", YANDEX_API_KEY, arg[0], arg[1], arg[2]);
                URL url = new URL(sURL);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();
                JsonParser jsonParser = new JsonParser();
                JsonElement root =
                        jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject jsonObject = root.getAsJsonObject();
                return jsonObject.get("text").getAsString();
            } catch (IOException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            delegate.processFinish(s);
        }

        public interface AsyncResponse {
            void processFinish(String output);
        }
    }

    public static class AsyncRequestToGetRightLanguageReduction extends AsyncTask<String, Integer, String> {
        public AsyncResponse delegate = null;

        public AsyncRequestToGetRightLanguageReduction(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(String... arg) {
            try {
                String sURL = String.format("https://translate.yandex.net/api/v1.5/tr.json/detect?" +
                        "key=%s" +
                        "&text=%s", YANDEX_API_KEY, arg[0]);
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

        @Override
        protected void onPostExecute(String s) {
            delegate.processFinish(s);
        }

        public interface AsyncResponse {
            void processFinish(String output);
        }
    }
}
