package me.annenkov.translator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.annenkov.translator.manager.HistoryManager;
import me.annenkov.translator.manager.LanguagesManager;
import me.annenkov.translator.manager.NetworkManager;
import me.annenkov.translator.model.HistoryElement;

public class MainActivity extends AppCompatActivity {
    private String mYandexApiKey;
    private ImageButton mSwapLanguageButton;
    private Button mFirstLanguageButton;
    private Button mSecondLanguageButton;
    private EditText mInputText;
    private TextView mTranslatedText;
    private ImageButton mClearText;
    private ImageButton mAddToFavoritesButton;

    private CardView mTranslatedTextCardView;

    private HistoryManager mHistoryManager;
    private LanguagesManager mLanguagesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mYandexApiKey = getResources().getString(R.string.yandex_api_key);

        mSwapLanguageButton = (ImageButton) findViewById(R.id.swapLanguage);
        mFirstLanguageButton = (Button) findViewById(R.id.firstLanguage);

        mSecondLanguageButton = (Button) findViewById(R.id.secondLanguage);

        mInputText = (EditText) findViewById(R.id.inputText);
        mTranslatedText = (TextView) findViewById(R.id.translatedText);

        mClearText = (ImageButton) findViewById(R.id.clearTextMain);
        mAddToFavoritesButton = (ImageButton) findViewById(R.id.addToFavoritesButtonMain);

        String firstLanguage = getResources().getString(R.string.russian);
        String secondLanguage = getResources().getString(R.string.english);

        Map<String, String> languageReductions = new ArrayMap<>();
        languageReductions.put(getResources().getString(R.string.russian), "ru");
        languageReductions.put(getResources().getString(R.string.english), "en");
        languageReductions.put(getResources().getString(R.string.polish), "pl");
        languageReductions.put(getResources().getString(R.string.italian), "it");
        languageReductions.put(getResources().getString(R.string.german), "de");
        languageReductions.put(getResources().getString(R.string.portuguese), "pt");
        languageReductions.put(getResources().getString(R.string.norwegian), "no");
        languageReductions.put(getResources().getString(R.string.ukrainian), "uk");
        languageReductions.put(getResources().getString(R.string.greek), "el");
        languageReductions.put(getResources().getString(R.string.chinese), "zh");
        languageReductions.put(getResources().getString(R.string.japanese), "ja");
        languageReductions.put(getResources().getString(R.string.turkish), "tr");
        languageReductions.put(getResources().getString(R.string.indonesian), "id");
        languageReductions.put(getResources().getString(R.string.hebrew), "he");
        languageReductions.put(getResources().getString(R.string.latin), "la");
        languageReductions.put(getResources().getString(R.string.lithuanian), "lt");

        mTranslatedTextCardView = (CardView) findViewById(R.id.translatedTextCardView);

        mHistoryManager = new HistoryManager();
        mLanguagesManager = new LanguagesManager(firstLanguage, secondLanguage, languageReductions);

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
                intent.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) mLanguagesManager.getLanguagesList());
                startActivityForResult(intent, 1);
            }
        });

        mSecondLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectLanguageActivity.class);
                intent.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) mLanguagesManager.getLanguagesList());
                startActivityForResult(intent, 2);
            }
        });

        mClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearText();
            }
        });

        mAddToFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHistoryManager.getCurrentHistoryElement().getFirstText().isEmpty()) return;
                mHistoryManager.cancelTimer();
                int elementIndex = mHistoryManager.getElementInHistoryIndex(mHistoryManager.getCurrentHistoryElement());
                if (elementIndex != 0) {
                    mHistoryManager.getCurrentHistoryElement().setFavorite(!mHistoryManager.getCurrentHistoryElement().isFavorite());
                    mHistoryManager.addHistoryElement(mHistoryManager.getCurrentHistoryElement());
                } else {
                    mHistoryManager.getFirstHistoryElement().setFavorite(!mHistoryManager.getFirstHistoryElement().isFavorite());
                }
                updateAddToFavoritesButton(mHistoryManager.getCurrentHistoryElement());
            }
        });

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mHistoryManager.getTimer() != null) mHistoryManager.cancelTimer();
                textStatusAction(s.toString(), mTranslatedTextCardView.getVisibility() == View.VISIBLE);
                String request = new NetworkManager(mYandexApiKey,
                        s.toString(),
                        mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getFirstLanguage()),
                        mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getSecondLanguage())).getTranslateText();
                mTranslatedText.setText(request);
                mHistoryManager.setCurrentHistoryElement(new HistoryElement(mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getFirstLanguage()).toUpperCase(),
                        mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getSecondLanguage()).toUpperCase(),
                        s.toString(),
                        request));
                updateAddToFavoritesButton(mHistoryManager.getCurrentHistoryElement());
                if ((!request.equals("") || !request.isEmpty())) {
                    mHistoryManager.addHistoryElementWithTimer(mHistoryManager.getCurrentHistoryElement(), 1650);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void textStatusAction(String text, boolean isShowed) {
        if (!text.isEmpty() && !isShowed) textNotEmptyAction();
        else if (text.isEmpty() && isShowed) textEmptyAction();
    }

    public void textNotEmptyAction() {
        mTranslatedTextCardView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_element));
        mClearText.setVisibility(View.VISIBLE);
        mTranslatedTextCardView.setVisibility(View.VISIBLE);
    }

    public void textEmptyAction() {
        mTranslatedTextCardView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.hide_element));
        mClearText.setVisibility(View.INVISIBLE);
        mTranslatedTextCardView.setVisibility(View.INVISIBLE);
    }

    public void swapLanguages() {
        String buffer = mLanguagesManager.getFirstLanguage();
        mLanguagesManager.setFirstLanguage(mLanguagesManager.getSecondLanguage());
        mLanguagesManager.setSecondLanguage(buffer);
        reloadUI();
    }

    public void reloadUI() {
        updateUI();
        clearText();
        textStatusAction("", mTranslatedTextCardView.getVisibility() == View.VISIBLE);
    }

    public void clearText() {
        mInputText.setText("");
    }

    public void updateUI() {
        mFirstLanguageButton.setText(mLanguagesManager.getFirstLanguage());
        mSecondLanguageButton.setText(mLanguagesManager.getSecondLanguage());
        updateAddToFavoritesButton(mHistoryManager.getCurrentHistoryElement());
    }

    public void updateAddToFavoritesButton(HistoryElement historyElement) {
        if (historyElement.isFavorite()) {
            mAddToFavoritesButton.setImageResource(R.drawable.bookmark);
        } else {
            mAddToFavoritesButton.setImageResource(R.drawable.bookmark_outline);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case 1:
                    if (mLanguagesManager.getSecondLanguage().equals(data.getStringExtra("LANGUAGE")))
                        mLanguagesManager.setSecondLanguage(mLanguagesManager.getFirstLanguage());
                    mLanguagesManager.setFirstLanguage(data.getStringExtra("LANGUAGE"));
                    reloadUI();
                    break;
                case 2:
                    if (mLanguagesManager.getFirstLanguage().equals(data.getStringExtra("LANGUAGE")))
                        mLanguagesManager.setFirstLanguage(mLanguagesManager.getSecondLanguage());
                    mLanguagesManager.setSecondLanguage(data.getStringExtra("LANGUAGE"));
                    reloadUI();
                    break;
                case 3:
                    mHistoryManager.updateHistory((List<HistoryElement>) data.getSerializableExtra("NEW_HISTORY"));
                    break;
                case 4:
                    mHistoryManager.setHistoryElements((List<HistoryElement>) data.getSerializableExtra("NEW_HISTORY"));
                    break;
            }
        } catch (NullPointerException e) {
            updateUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, getResources().getString(R.string.favorites));
        menu.add(1, 2, 2, getResources().getString(R.string.history));
        menu.add(1, 3, 3, getResources().getString(R.string.about));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent firstIntent = new Intent(MainActivity.this, HistoryActivity.class);
                Bundle firstBundle = new Bundle();
                firstBundle.putBoolean("IS_ONLY_FAVORITES", true);
                firstBundle.putParcelableArrayList("HISTORY", (ArrayList<? extends Parcelable>) mHistoryManager.getHistoryElements());
                firstIntent.putExtras(firstBundle);
                startActivityForResult(firstIntent, 3);
                break;
            case 2:
                Intent secondIntent = new Intent(MainActivity.this, HistoryActivity.class);
                Bundle secondBundle = new Bundle();
                secondBundle.putBoolean("IS_ONLY_FAVORITES", false);
                secondBundle.putParcelableArrayList("HISTORY", (ArrayList<? extends Parcelable>) mHistoryManager.getHistoryElements());
                secondIntent.putExtras(secondBundle);
                startActivityForResult(secondIntent, 4);
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
