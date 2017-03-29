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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final String YANDEX_API_KEY = "trnsl.1.1.20170317T155546Z.e419594abd6d2bd3.da7c18ede5fa233864ef799143b796f59e910c29";
    public List<String> mLanguages = new ArrayList<>();
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

        for (String language : mLanguageReductions.keySet()) {
            mLanguages.add(language);
        }

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
                intent.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) mLanguages);
                startActivityForResult(intent, 1);
            }
        });

        mSecondLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectSecondLanguageActivity.class);
                intent.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) mLanguages);
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

    public void swapLanguages() {
        CharSequence buffer = mFirstLanguageButton.getText();
        mFirstLanguageButton.setText(mSecondLanguageButton.getText());
        mSecondLanguageButton.setText(buffer);
    }

    public void updateUI() {
        mFirstLanguageButton.setText(getFirstLanguage());
        mSecondLanguageButton.setText(getSecondLanguage());
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

    public List<String> getLanguages() {
        return mLanguages;
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
            mTranslatedText.setText(s);
        }
    }
}
