package me.annenkov.translator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final String YANDEX_API_KEY = "trnsl.1.1.20170317T155546Z.e419594abd6d2bd3.da7c18ede5fa233864ef799143b796f59e910c29";
    private Map<String, String> mLanguageReductions = new ArrayMap<>();
    private String mFirstLanguage;
    private String mSecondLanguage;
    private ImageButton mSwapLanguageButton;
    private Button mFirstLanguageButton;
    private Button mSecondLanguageButton;
    private EditText mInputText;
    private TextView mTranslatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwapLanguageButton = (ImageButton) findViewById(R.id.swapLanguage);
        mFirstLanguageButton = (Button) findViewById(R.id.firstLanguage);

        mSecondLanguageButton = (Button) findViewById(R.id.secondLanguage);

        mInputText = (EditText) findViewById(R.id.inputText);
        mTranslatedText = (TextView) findViewById(R.id.translatedText);

        mLanguageReductions.put(getResources().getString(R.string.russian), "ru");
        mLanguageReductions.put(getResources().getString(R.string.english), "en");
        mLanguageReductions.put(getResources().getString(R.string.polish), "pl");
        mLanguageReductions.put(getResources().getString(R.string.italian), "it");
        mLanguageReductions.put(getResources().getString(R.string.german), "de");
        mLanguageReductions.put(getResources().getString(R.string.portuguese), "pt");
        mLanguageReductions.put(getResources().getString(R.string.norwegian), "no");
        mLanguageReductions.put(getResources().getString(R.string.ukrainian), "uk");
        mLanguageReductions.put(getResources().getString(R.string.greek), "el");
        mLanguageReductions.put(getResources().getString(R.string.chinese), "zh");
        mLanguageReductions.put(getResources().getString(R.string.japanese), "ja");
        mLanguageReductions.put(getResources().getString(R.string.turkish), "tr");
        mLanguageReductions.put(getResources().getString(R.string.indonesian), "id");
        mLanguageReductions.put(getResources().getString(R.string.hebrew), "he");
        mLanguageReductions.put(getResources().getString(R.string.latin), "la");
        mLanguageReductions.put(getResources().getString(R.string.lithuanian), "lt");

        mFirstLanguage = getResources().getString(R.string.russian);
        mSecondLanguage = getResources().getString(R.string.english);

        updateUI();

        mSwapLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapLanguages();
            }
        });

        mFirstLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectFirstLanguageActivity.class);
                intent.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) getListFromMap(mLanguageReductions));
                startActivityForResult(intent, 1);
            }
        });

        mSecondLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectSecondLanguageActivity.class);
                intent.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) getListFromMap(mLanguageReductions));
                startActivityForResult(intent, 2);
            }
        });

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Document doc = null;
                try {
                    new AsyncRequest().execute(YANDEX_API_KEY, s.toString());
                } catch (Exception e) {
                    mTranslatedText.setText(e.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private List getListFromMap(Map map) {
        List<String> languages = new ArrayList<>();
        languages.addAll(map.keySet());
        Collections.sort(languages);
        return languages;
    }

    public void swapLanguages() {
        String buffer = getFirstLanguage();
        setFirstLanguage(getSecondLanguage());
        setSecondLanguage(buffer);
        updateUI();
    }

    public void updateUI() {
        mFirstLanguageButton.setText(getFirstLanguage());
        mSecondLanguageButton.setText(getSecondLanguage());
        mInputText.setText("");
    }

    public String getFirstLanguage() {
        return mFirstLanguage;
    }

    public void setFirstLanguage(String sFirstLanguage) {
        this.mFirstLanguage = sFirstLanguage;
    }

    public String getSecondLanguage() {
        return mSecondLanguage;
    }

    public void setSecondLanguage(String sSecondLanguage) {
        this.mSecondLanguage = sSecondLanguage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            setFirstLanguage(data.getStringExtra("LANGUAGE"));
        } else if (resultCode == 2) {
            setSecondLanguage(data.getStringExtra("LANGUAGE"));
        }
        updateUI();
    }

    public String getReduction(String key) {
        return mLanguageReductions.get(key);
    }

    public Button getFirstLanguageButton() {
        return mFirstLanguageButton;
    }

    public void setFirstLanguageButton(Button firstLanguageButton) {
        mFirstLanguageButton = firstLanguageButton;
    }

    public Button getSecondLanguageButton() {
        return mSecondLanguageButton;
    }

    public void setSecondLanguageButton(Button secondLanguageButton) {
        mSecondLanguageButton = secondLanguageButton;
    }

    private class AsyncRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            try {
                String doc = Jsoup.connect("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                        "key=" + arg[0] +
                        "&text=" + arg[1] +
                        "&lang=" + mLanguageReductions.get(mFirstLanguage) +
                        "-" + mLanguageReductions.get(mSecondLanguage))
                        .ignoreContentType(true)
                        .execute()
                        .body();
                String text = new JSONObject(doc).getString("text");
                return text.substring(2, text.length() - 2);
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mTranslatedText.setText(s);
        }
    }
}
