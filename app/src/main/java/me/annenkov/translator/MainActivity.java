package me.annenkov.translator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {
    private final String YANDEX_API_KEY = "trnsl.1.1.20170317T155546Z.e419594abd6d2bd3.da7c18ede5fa233864ef799143b796f59e910c29";

    private EditText inputText;
    private TextView translatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = (EditText) findViewById(R.id.inputText);
        translatedText = (TextView) findViewById(R.id.translatedText);

        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Document doc = null;
                try {
                    new AsyncRequest().execute(YANDEX_API_KEY, s.toString());
                } catch (Exception e) {
                    translatedText.setText(e.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class AsyncRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            try {
                String doc = Jsoup.connect("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                        "key=" + arg[0] +
                        "&text=" + arg[1] +
                        "&lang=en").ignoreContentType(true).execute().body();
                String text = new JSONObject(doc).getString("text");
                return text.substring(2, text.length() - 2);
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            translatedText.setText(s);
        }
    }
}
