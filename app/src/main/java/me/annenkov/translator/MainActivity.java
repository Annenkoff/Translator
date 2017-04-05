package me.annenkov.translator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private final String YANDEX_API_KEY = "trnsl.1.1.20170317T155546Z.e419594abd6d2bd3.da7c18ede5fa233864ef799143b796f59e910c29";
    Timer mTimer;
    private Map<String, String> mLanguageReductions = new ArrayMap<>();
    private List<HistoryElement> mHistoryElements = new ArrayList<>();
    private String mFirstLanguage;
    private String mSecondLanguage;
    private ImageButton mSwapLanguageButton;
    private Button mFirstLanguageButton;
    private Button mSecondLanguageButton;
    private EditText mInputText;
    private TextView mTranslatedText;
    private ImageButton mAddToFavoritesButton;

    private HistoryElement mCurrentHistoryElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwapLanguageButton = (ImageButton) findViewById(R.id.swapLanguage);
        mFirstLanguageButton = (Button) findViewById(R.id.firstLanguage);

        mSecondLanguageButton = (Button) findViewById(R.id.secondLanguage);

        mInputText = (EditText) findViewById(R.id.inputText);
        mTranslatedText = (TextView) findViewById(R.id.translatedText);

        mAddToFavoritesButton = (ImageButton) findViewById(R.id.addToFavoritesButtonMain);

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

        mCurrentHistoryElement = new HistoryElement("", "", "", "");

        reloadUI();

        mSwapLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapLanguages();
            }
        });

        mFirstLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectLanguageActivity.class);
                intent.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) getListFromMap(mLanguageReductions));
                startActivityForResult(intent, 1);
            }
        });

        mSecondLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectLanguageActivity.class);
                intent.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) getListFromMap(mLanguageReductions));
                startActivityForResult(intent, 2);
            }
        });

        mAddToFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentHistoryElement.getFirstText().isEmpty()) return;
                mTimer.cancel();
                int elementIndex = getElementInHistoryIndex(mCurrentHistoryElement);
                if (elementIndex != 0) {
                    mCurrentHistoryElement.setFavorite(!mCurrentHistoryElement.isFavorite());
                    mHistoryElements.add(0, mCurrentHistoryElement);
                } else {
                    mHistoryElements.get(0).setFavorite(!mHistoryElements.get(0).isFavorite());
                }
                updateAddToFavoritesButton(mCurrentHistoryElement);
            }
        });

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (mTimer != null) mTimer.cancel();
                    String request = new AsyncRequest().execute(YANDEX_API_KEY, s.toString()).get();
                    mTranslatedText.setText(request);
                    mCurrentHistoryElement = new HistoryElement(mLanguageReductions.get(getFirstLanguage()).toUpperCase(),
                            mLanguageReductions.get(getSecondLanguage()).toUpperCase(),
                            s.toString(),
                            request);
                    updateAddToFavoritesButton(mCurrentHistoryElement);
                    if ((!request.equals("") || !request.isEmpty())) {
                        mTimer = new Timer(true);
                        mTimer.schedule(new TimerTask(mCurrentHistoryElement), 1650);
                    }
                } catch (Exception e) {
                    mTranslatedText.setText("");
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
        reloadUI();
    }

    public void reloadUI() {
        updateUI();
        mInputText.setText("");
    }

    public void updateUI() {
        mFirstLanguageButton.setText(getFirstLanguage());
        mSecondLanguageButton.setText(getSecondLanguage());
        updateAddToFavoritesButton(mCurrentHistoryElement);
    }

    public boolean isElementInHistory(HistoryElement historyElement) {
        return getElementInHistory(historyElement) != null;
    }

    public HistoryElement getElementInHistory(HistoryElement historyElement) {
        for (HistoryElement historyElementInList : mHistoryElements) {
            if (historyElementInList.equals(historyElement)) {
                return historyElementInList;
            }
        }
        return null;
    }

    public Integer getElementInHistoryIndex(HistoryElement historyElement) {
        for (int i = 0; i < mHistoryElements.size(); i++) {
            if (mHistoryElements.get(i).equals(historyElement)) {
                return i;
            }
        }
        return -1;
    }

    public void updateAddToFavoritesButton(HistoryElement historyElement) {
        if (historyElement.isFavorite()) {
            mAddToFavoritesButton.setImageResource(R.drawable.ic_bookmark_black_24dp);
        } else {
            mAddToFavoritesButton.setImageResource(R.drawable.ic_bookmark_outline_black_24dp);
        }
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
        try {
            switch (requestCode) {
                case 1:
                    setFirstLanguage(data.getStringExtra("LANGUAGE"));
                    break;
                case 2:
                    setSecondLanguage(data.getStringExtra("LANGUAGE"));
                    break;
                case 3:
                    List<HistoryElement> historyElements = (List<HistoryElement>) data.getSerializableExtra("NEW_HISTORY");
                    for (int i = 0; i < mHistoryElements.size(); i++) {
                        for (int j = 0; j < historyElements.size(); j++) {
                            if (mHistoryElements.get(i).equals(historyElements.get(j))) {
                                mHistoryElements.set(i, historyElements.get(j));
                            }
                        }
                    }
                    break;
                case 4:
                    mHistoryElements = (List<HistoryElement>) data.getSerializableExtra("NEW_HISTORY");
                    break;
            }
        } catch (NullPointerException e) {
            //TODO: добавить логгирование
        }
        reloadUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, getResources().getString(R.string.favorites));
        menu.add(1, 2, 2, getResources().getString(R.string.history));
        menu.add(1, 3, 3, getResources().getString(R.string.settings));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent firstIntent = new Intent(MainActivity.this, HistoryActivity.class);
                Bundle firstBundle = new Bundle();
                firstBundle.putBoolean("IS_ONLY_FAVORITES", true);
                firstBundle.putParcelableArrayList("HISTORY", (ArrayList<? extends Parcelable>) mHistoryElements);
                firstIntent.putExtras(firstBundle);
                startActivityForResult(firstIntent, 3);
                break;
            case 2:
                Intent secondIntent = new Intent(MainActivity.this, HistoryActivity.class);
                Bundle secondBundle = new Bundle();
                secondBundle.putBoolean("IS_ONLY_FAVORITES", false);
                secondBundle.putParcelableArrayList("HISTORY", (ArrayList<? extends Parcelable>) mHistoryElements);
                secondIntent.putExtras(secondBundle);
                startActivityForResult(secondIntent, 4);
                break;
            case 3:
                Toast.makeText(this, "Скоро будет работать", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class TimerTask extends java.util.TimerTask {
        private HistoryElement mHistoryElement;

        public TimerTask(HistoryElement historyElement) {
            mHistoryElement = historyElement;
        }

        @Override
        public void run() {
            mHistoryElements.add(0, mHistoryElement);
        }
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
    }
}
