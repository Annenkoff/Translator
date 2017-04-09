package me.annenkov.translator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import me.annenkov.translator.R;
import me.annenkov.translator.manager.HistoryManager;
import me.annenkov.translator.manager.LanguagesManager;
import me.annenkov.translator.manager.NetworkManager;
import me.annenkov.translator.model.HistoryElement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private HistoryManager mHistoryManager;
    private LanguagesManager mLanguagesManager;

    private CardView mTranslatedTextCardView;

    private EditText mInputText;
    private TextView mTranslatedText;

    private Button mFirstLanguageButton;
    private Button mSecondLanguageButton;
    private ImageButton mSwapLanguageButton;
    private ImageButton mClearText;
    private ImageButton mAddToFavoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SugarContext.init(this);

        mHistoryManager = new HistoryManager();
        mLanguagesManager = new LanguagesManager(this);

        mTranslatedTextCardView = (CardView) findViewById(R.id.translatedTextCardView);

        mInputText = (EditText) findViewById(R.id.inputText);
        mTranslatedText = (TextView) findViewById(R.id.translatedText);

        mFirstLanguageButton = (Button) findViewById(R.id.firstLanguage);
        mFirstLanguageButton.setOnClickListener(this);
        mSecondLanguageButton = (Button) findViewById(R.id.secondLanguage);
        mSecondLanguageButton.setOnClickListener(this);
        mSwapLanguageButton = (ImageButton) findViewById(R.id.swapLanguage);
        mSwapLanguageButton.setOnClickListener(this);
        mClearText = (ImageButton) findViewById(R.id.clearTextMain);
        mClearText.setOnClickListener(this);
        mAddToFavoritesButton = (ImageButton) findViewById(R.id.addToFavoritesButtonMain);
        mAddToFavoritesButton.setOnClickListener(this);

        updateUI();

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextChangedAction(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onTextChangedAction(String s) {
        if (mHistoryManager.getTimer() != null) mHistoryManager.cancelTimer();
        offAddToFavoritesButton();
        textStatusAction(s, mTranslatedTextCardView.getVisibility() == View.VISIBLE);
        String request = new NetworkManager(MainActivity.this,
                s,
                mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getFirstLanguage()),
                mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getSecondLanguage())).getTranslatedText();
        mTranslatedText.setText(request);
        mHistoryManager.setCurrentHistoryElement(new HistoryElement(mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getFirstLanguage()).toUpperCase(),
                mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getSecondLanguage()).toUpperCase(),
                s,
                request));
        if ((!request.equals("") || !request.isEmpty())) {
            mHistoryManager.addHistoryElementWithTimer(mHistoryManager.getCurrentHistoryElement(), 1650);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }

    public void textStatusAction(String text, boolean isShowed) {
        if ((!text.isEmpty() || !text.equals("")) && !isShowed) textNotEmptyAction();
        else if ((text.isEmpty() || text.equals("")) && isShowed) textEmptyAction();
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
        updateUI();
    }

    private void offAddToFavoritesButton() {
        mAddToFavoritesButton.setImageResource(R.drawable.bookmark_outline_white);
    }

    private void onAddToFavoritesButton() {
        mAddToFavoritesButton.setImageResource(R.drawable.bookmark_white);
    }

    public void clearText() {
        mInputText.setText("");
    }

    public void updateUI() {
        mFirstLanguageButton.setText(mLanguagesManager.getFirstLanguage());
        mSecondLanguageButton.setText(mLanguagesManager.getSecondLanguage());
        onTextChangedAction(mInputText.getText().toString());
        updateAddToFavoritesButton();
    }

    public void updateAddToFavoritesButton() {
        try {
            if (mHistoryManager.getFirstHistoryElement().isFavorite()) {
                onAddToFavoritesButton();
            } else {
                offAddToFavoritesButton();
            }
        } catch (NullPointerException ignored) {
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
                    updateUI();
                    break;
                case 2:
                    if (mLanguagesManager.getFirstLanguage().equals(data.getStringExtra("LANGUAGE")))
                        mLanguagesManager.setFirstLanguage(mLanguagesManager.getSecondLanguage());
                    mLanguagesManager.setSecondLanguage(data.getStringExtra("LANGUAGE"));
                    updateUI();
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
        Intent intent;
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
            case 1:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                bundle.putBoolean("IS_ONLY_FAVORITES", true);
                bundle.putParcelableArrayList("HISTORY", (ArrayList<? extends Parcelable>) mHistoryManager.getHistoryElements());
                intent.putExtras(bundle);
                startActivityForResult(intent, 3);
                break;
            case 2:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                bundle.putBoolean("IS_ONLY_FAVORITES", false);
                bundle.putParcelableArrayList("HISTORY", (ArrayList<? extends Parcelable>) mHistoryManager.getHistoryElements());
                intent.putExtras(bundle);
                startActivityForResult(intent, 4);
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swapLanguage:
                swapLanguages();
                break;
            case R.id.firstLanguage:
                Intent intent1 = new Intent(MainActivity.this, SelectLanguageActivity.class);
                intent1.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) mLanguagesManager.getLanguagesList());
                startActivityForResult(intent1, 1);
                break;
            case R.id.secondLanguage:
                Intent intent2 = new Intent(MainActivity.this, SelectLanguageActivity.class);
                intent2.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) mLanguagesManager.getLanguagesList());
                startActivityForResult(intent2, 2);
                break;
            case R.id.clearTextMain:
                clearText();
                break;
            case R.id.addToFavoritesButtonMain:
                if (mHistoryManager.getCurrentHistoryElement().getFirstText().isEmpty()) return;
                mHistoryManager.cancelTimer();
                if (mHistoryManager.getElementInHistoryIndex(mHistoryManager.getCurrentHistoryElement()) != 0) {
                    mHistoryManager.getCurrentHistoryElement().setFavorite(!mHistoryManager.getCurrentHistoryElement().isFavorite());
                    mHistoryManager.addHistoryElement(mHistoryManager.getCurrentHistoryElement());
                } else {
                    HistoryElement historyElement = mHistoryManager.getFirstHistoryElement();
                    historyElement.setFavorite(!mHistoryManager.getFirstHistoryElement().isFavorite());
                    mHistoryManager.addHistoryElement(historyElement);
                }
                updateAddToFavoritesButton();
                break;
        }
    }
}
