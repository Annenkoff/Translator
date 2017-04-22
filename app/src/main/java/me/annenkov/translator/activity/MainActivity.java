package me.annenkov.translator.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.slideup.SlideUp;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import me.annenkov.translator.Extras;
import me.annenkov.translator.R;
import me.annenkov.translator.helper.DrawerHelper;
import me.annenkov.translator.helper.SliderHelper;
import me.annenkov.translator.manager.HistoryManager;
import me.annenkov.translator.manager.LanguagesManager;
import me.annenkov.translator.manager.SpeechManager;
import me.annenkov.translator.manager.TimerManager;
import me.annenkov.translator.model.HistoryElement;
import me.annenkov.translator.tools.Action;
import me.annenkov.translator.tools.Utils;

/**
 * Главный Activity. Точка входа в приложение.
 * Представляет экран для перевода.
 * Здесь инициализируем основные элементы, геттеры и слушатели.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, Drawer.OnDrawerItemClickListener {
    private View mDim;

    private Toolbar mToolbar;
    private Drawer mDrawer;
    private SlideUp mSlide;

    private ScrollView mTranslatedTextScrollView;

    private EditText mInputText;
    private TextView mTranslatedText;

    private FloatingActionButton mRecommendationFloatButton;
    private Button mRightLanguageButton;
    private Button mFirstLanguageButton;
    private Button mSecondLanguageButton;
    private ImageButton mSwapLanguageButton;
    private ImageButton mVocalizeFirstText;
    private ImageButton mVocalizeSecondText;
    private ImageButton mClearTextButton;
    private ImageButton mAddToFavoritesButton;
    private ImageButton mCopyTextButton;
    private ImageButton mShareButton;

    /**
     * Инициализируем элементы, которые нам потребуются для работы:
     * Тулбар, шторка, слайдер, кнопки, поля и т.д.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SugarContext.init(this);
        mToolbar = (Toolbar) this.findViewById(R.id.toolbar_main);
        initToolbar();

        mDim = findViewById(R.id.dim);

        mDrawer = new DrawerHelper().getDrawer(this);
        mSlide = new SliderHelper().getSlider(this);
        mTranslatedTextScrollView = (ScrollView) findViewById(R.id.translatedTextScrollView);

        mInputText = (EditText) findViewById(R.id.inputText);
        mTranslatedText = (TextView) findViewById(R.id.translatedText);

        mRecommendationFloatButton = (FloatingActionButton) findViewById(R.id.recommendation_float_button_main);
        mRecommendationFloatButton.hide();
        mRecommendationFloatButton.setOnClickListener(this);
        mRightLanguageButton = (Button) findViewById(R.id.right_language_button);
        mRightLanguageButton.setOnClickListener(this);
        mFirstLanguageButton = (Button) findViewById(R.id.firstLanguage);
        mFirstLanguageButton.setOnClickListener(this);
        mSecondLanguageButton = (Button) findViewById(R.id.secondLanguage);
        mSecondLanguageButton.setOnClickListener(this);
        mSwapLanguageButton = (ImageButton) findViewById(R.id.swapLanguage);
        mSwapLanguageButton.setOnClickListener(this);
        mVocalizeFirstText = (ImageButton) findViewById(R.id.vocalizeFirstText);
        mVocalizeFirstText.setOnClickListener(this);
        mVocalizeSecondText = (ImageButton) findViewById(R.id.vocalizeSecondText);
        mVocalizeSecondText.setOnClickListener(this);
        mClearTextButton = (ImageButton) findViewById(R.id.clearTextMain);
        mClearTextButton.setOnClickListener(this);
        mAddToFavoritesButton = (ImageButton) findViewById(R.id.addToFavoritesButtonMain);
        mAddToFavoritesButton.setOnClickListener(this);
        mCopyTextButton = (ImageButton) findViewById(R.id.copyTextButtonMenu);
        mCopyTextButton.setOnClickListener(this);
        mShareButton = (ImageButton) findViewById(R.id.shareButtonMenu);
        mShareButton.setOnClickListener(this);

        LanguagesManager.init(this);
        SpeechManager.init(this);

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (TimerManager.getTranslateTimer() != null)
                    TimerManager.cancelTranslateTimer();
                if (TimerManager.getAddHistoryElementTimer() != null)
                    TimerManager.cancelAddHistoryElementTimer();
                offAddToFavoritesButton();
                if (s.length() == 0) {
                    mClearTextButton.setVisibility(View.INVISIBLE);
                    Action.textEmptyAction(MainActivity.this);
                    return;
                }
                mClearTextButton.setVisibility(View.VISIBLE);
                TimerManager.startTranslateTimer(MainActivity.this, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.menu));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public Button getFirstLanguageButton() {
        return mFirstLanguageButton;
    }

    public Button getSecondLanguageButton() {
        return mSecondLanguageButton;
    }

    public SlideUp getSlide() {
        return mSlide;
    }

    public View getDim() {
        return mDim;
    }

    public FloatingActionButton getRecommendationFloatButton() {
        return mRecommendationFloatButton;
    }

    public Button getRightLanguageButton() {
        return mRightLanguageButton;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }

    public EditText getInputText() {
        return mInputText;
    }

    public TextView getTranslatedText() {
        return mTranslatedText;
    }

    public ScrollView getTranslatedTextScrollView() {
        return mTranslatedTextScrollView;
    }

    public ImageButton getVocalizeFirstText() {
        return mVocalizeFirstText;
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
        Action.onTextChangedAction(this, mInputText.getText().toString());
        updateAddToFavoritesButton();
    }

    public void updateAddToFavoritesButton() {
        try {
            if (HistoryManager.getFirstHistoryElement().isFavorite()) {
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
                    LanguagesManager.setFirstLanguage(data.getStringExtra(Extras.EXTRA_LANGUAGE));
                    updateUI();
                    break;
                case 2:
                    LanguagesManager.setSecondLanguage(data.getStringExtra(Extras.EXTRA_LANGUAGE));
                    updateUI();
                    break;
                case 3:
                    HistoryManager.updateHistory((List<HistoryElement>) data.getSerializableExtra(Extras.EXTRA_NEW_HISTORY));
                    if (data.getStringExtra(Extras.EXTRA_TEXT_TO_TRANSLATE) != null) {
                        LanguagesManager.setFirstLanguage(LanguagesManager.getLanguage(data.getStringExtra(Extras.EXTRA_FIRST_LANGUAGE)));
                        LanguagesManager.setSecondLanguage(LanguagesManager.getLanguage(data.getStringExtra(Extras.EXTRA_SECOND_LANGUAGE)));
                        mInputText.setText(data.getStringExtra(Extras.EXTRA_TEXT_TO_TRANSLATE));
                    }
                    break;
                case 4:
                    HistoryManager.setHistoryElements((List<HistoryElement>) data.getSerializableExtra(Extras.EXTRA_NEW_HISTORY));
                    if (data.getStringExtra(Extras.EXTRA_TEXT_TO_TRANSLATE) != null) {
                        LanguagesManager.setFirstLanguage(LanguagesManager.getLanguage(data.getStringExtra(Extras.EXTRA_FIRST_LANGUAGE)));
                        LanguagesManager.setSecondLanguage(LanguagesManager.getLanguage(data.getStringExtra(Extras.EXTRA_SECOND_LANGUAGE)));
                        mInputText.setText(data.getStringExtra(Extras.EXTRA_TEXT_TO_TRANSLATE));
                    }
                    break;
            }
        } catch (NullPointerException e) {
            updateUI();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recommendation_float_button_main:
                mSlide.show();
                break;
            case R.id.swapLanguage:
                LanguagesManager.swapLanguages();
                updateUI();
                break;
            case R.id.firstLanguage:
                Intent intent1 = new Intent(MainActivity.this, SelectLanguageActivity.class);
                intent1.putStringArrayListExtra(Extras.EXTRA_LANGUAGES, (ArrayList<String>) LanguagesManager.getLanguagesList());
                startActivityForResult(intent1, 1);
                break;
            case R.id.secondLanguage:
                Intent intent2 = new Intent(MainActivity.this, SelectLanguageActivity.class);
                intent2.putStringArrayListExtra(Extras.EXTRA_LANGUAGES, (ArrayList<String>) LanguagesManager.getLanguagesList());
                startActivityForResult(intent2, 2);
                break;
            case R.id.vocalizeFirstText:
                SpeechManager.vocalizeText(this,
                        LanguagesManager.getVocalizerLanguage(this, mInputText.getText().toString(), LanguagesManager.getFirstLanguageReduction()),
                        mInputText.getText().toString());
                break;
            case R.id.vocalizeSecondText:
                SpeechManager.vocalizeText(this,
                        LanguagesManager.getVocalizerLanguage(this, mTranslatedText.getText().toString(), LanguagesManager.getSecondLanguageReduction()),
                        mTranslatedText.getText().toString());
                break;
            case R.id.clearTextMain:
                clearText();
                break;
            case R.id.addToFavoritesButtonMain:
                if (HistoryManager.getCurrentHistoryElement(MainActivity.this).getFirstText().isEmpty())
                    return;
                if (TimerManager.getAddHistoryElementTimer() != null)
                    TimerManager.cancelAddHistoryElementTimer();
                if (HistoryManager.getElementInHistoryIndex(HistoryManager.getCurrentHistoryElement(MainActivity.this)) != 0) {
                    if (!HistoryManager.getCurrentHistoryElement(MainActivity.this).getSecondText().equals("...")) {
                        HistoryElement historyElement = HistoryManager.getCurrentHistoryElement(MainActivity.this);
                        historyElement.setFavorite(!historyElement.isFavorite());
                        HistoryManager.addHistoryElement(historyElement);
                    }
                } else {
                    HistoryElement historyElement = HistoryManager.getFirstHistoryElement();
                    historyElement.setFavorite(!HistoryManager.getFirstHistoryElement().isFavorite());
                    HistoryManager.addHistoryElement(historyElement);
                }
                updateAddToFavoritesButton();
                break;
            case R.id.copyTextButtonMenu:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Text", mTranslatedText.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
                break;
            case R.id.shareButtonMenu:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mTranslatedText.getText());
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
                break;
        }
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        Intent intent;
        Bundle bundle = new Bundle();
        switch (position) {
            case 2:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                bundle.putBoolean(Extras.EXTRA_IS_ONLY_FAVORITES, true);
                bundle.putParcelableArrayList(Extras.EXTRA_HISTORY, (ArrayList<? extends Parcelable>) HistoryManager.getHistoryElements());
                intent.putExtras(bundle);
                startActivityForResult(intent, 3);
                break;
            case 3:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                bundle.putBoolean(Extras.EXTRA_IS_ONLY_FAVORITES, false);
                bundle.putParcelableArrayList(Extras.EXTRA_HISTORY, (ArrayList<? extends Parcelable>) HistoryManager.getHistoryElements());
                intent.putExtras(bundle);
                startActivityForResult(intent, 4);
                break;
            case 4:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case 6:
                Utils.startBrowser(this, "https://github.com/ZZooRM/Translator");
                break;
        }
        return false;
    }
}
