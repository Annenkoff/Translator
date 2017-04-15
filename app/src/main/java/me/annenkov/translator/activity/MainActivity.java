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
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.slideup.SlideUp;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import me.annenkov.translator.R;
import me.annenkov.translator.Utils;
import me.annenkov.translator.manager.CacheManager;
import me.annenkov.translator.manager.HistoryManager;
import me.annenkov.translator.manager.LanguagesManager;
import me.annenkov.translator.manager.NetworkManager;
import me.annenkov.translator.manager.TimerManager;
import me.annenkov.translator.model.HistoryElement;

/**
 * Главный Activity. Точка входа в приложение.
 * Представляет экран для перевода.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, Drawer.OnDrawerItemClickListener {
    private View mDim;

    private Toolbar mToolbar;
    private Drawer mDrawer;
    private SlideUp mSlide;

    private Utils mUtils;
    private LanguagesManager mLanguagesManager;

    private ScrollView mTranslatedTextScrollView;

    private EditText mInputText;
    private TextView mTranslatedText;

    private FloatingActionButton mRecommendationFloatButton;
    private Button mRightLanguageButton;
    private Button mFirstLanguageButton;
    private Button mSecondLanguageButton;
    private ImageButton mSwapLanguageButton;
    private ImageButton mClearTextButton;
    private ImageButton mAddToFavoritesButton;
    private ImageButton mCopyTextButton;
    private ImageButton mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SugarContext.init(this);
        mToolbar = (Toolbar) this.findViewById(R.id.toolbar_main);
        initToolbar();

        mDim = findViewById(R.id.dim);

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.main_page).withIcon(R.drawable.home).withSelectable(true)
                                .withSelectedTextColor(ContextCompat.getColor(this, R.color.greyDark)),
                        new PrimaryDrawerItem().withName(R.string.favorites).withIcon(R.drawable.bookmark_black).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.history).withIcon(R.drawable.history).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.about).withIcon(R.drawable.information).withSelectable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.github).withIcon(R.drawable.github_circle).withSelectable(false)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        mUtils.hideKeyboard();
                        if (mSlide.isVisible()) mSlide.hide();
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withOnDrawerItemClickListener(this)
                .build();
        mSlide = new SlideUp.Builder(findViewById(R.id.slide_recommendation))
                .withListeners(new SlideUp.Listener() {
                    @Override
                    public void onSlide(float percent) {
                        mDim.setAlpha(1 - (percent / 100));
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE
                                && !mLanguagesManager.isFirstLanguageIsRight(mInputText.getText().toString())) {
                            mRecommendationFloatButton.show();
                        }
                    }
                })
                .withStartState(SlideUp.State.HIDDEN)
                .withStartGravity(Gravity.TOP)
                .build();

        mUtils = new Utils(this);
        mLanguagesManager = new LanguagesManager(this);

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
        mClearTextButton = (ImageButton) findViewById(R.id.clearTextMain);
        mClearTextButton.setOnClickListener(this);
        mAddToFavoritesButton = (ImageButton) findViewById(R.id.addToFavoritesButtonMain);
        mAddToFavoritesButton.setOnClickListener(this);
        mCopyTextButton = (ImageButton) findViewById(R.id.copyTextButtonMenu);
        mCopyTextButton.setOnClickListener(this);
        mShareButton = (ImageButton) findViewById(R.id.shareButtonMenu);
        mShareButton.setOnClickListener(this);

        mFirstLanguageButton.setText(LanguagesManager.getFirstLanguage());
        mSecondLanguageButton.setText(LanguagesManager.getSecondLanguage());

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecommendationFloatButton.hide();
                if (TimerManager.getTranslateTimer() != null) TimerManager.cancelTranslateTimer();
                if (TimerManager.getAddHistoryElementTimer() != null)
                    TimerManager.cancelAddHistoryElementTimer();
                if (s.length() == 0) {
                    onTextChangedAction(s.toString());
                    return;
                }
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

    public void onTextChangedAction(final String firstText) {
        if (TimerManager.getAddHistoryElementTimer() != null)
            TimerManager.cancelAddHistoryElementTimer();
        offAddToFavoritesButton();
        String secondText = CacheManager
                .getTranslationFromText(LanguagesManager.getFirstLanguageReduction(), firstText);
        if (secondText == null) {
            secondText = new NetworkManager(MainActivity.this,
                    firstText,
                    LanguagesManager.getLanguageReductions().get(LanguagesManager.getFirstLanguage()),
                    LanguagesManager.getLanguageReductions().get(LanguagesManager.getSecondLanguage())).getTranslatedText();
            CacheManager.addToCache(LanguagesManager.getFirstLanguageReduction(),
                    firstText, secondText);
        }
        mTranslatedText.setText(secondText);
        textStatusAction(firstText, secondText, mTranslatedTextScrollView.getVisibility() == View.VISIBLE);
        HistoryElement historyElement = new HistoryElement(LanguagesManager.getLanguageReductions().get(LanguagesManager.getFirstLanguage()).toUpperCase(),
                LanguagesManager.getLanguageReductions().get(LanguagesManager.getSecondLanguage()).toUpperCase(),
                firstText,
                secondText);
        HistoryManager.setCurrentHistoryElement(historyElement);
        if (((!secondText.equals("") || !secondText.isEmpty())) && !HistoryManager.getFirstHistoryElement().equals(historyElement)) {
            HistoryManager.addHistoryElementWithTimer(HistoryManager.getCurrentHistoryElement());
        }
        if (!mLanguagesManager.isFirstLanguageIsRight(firstText)) {
            final String rightLanguage = mLanguagesManager.getRightLanguage(firstText);
            mRightLanguageButton.setText(getString(R.string.set) + " " + rightLanguage);
            mRecommendationFloatButton.show();
            mRightLanguageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLanguagesManager.makeFirstLanguageRight(firstText);
                    updateUI();
                    mSlide.hide();
                    mRecommendationFloatButton.hide();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }

    public void textStatusAction(String firstText, String secondText, boolean isShowed) {
        if (!firstText.isEmpty() && !secondText.isEmpty() && !isShowed) textNotEmptyAction();
        else if (firstText.isEmpty() && secondText.isEmpty() && isShowed) textEmptyAction();
        else if (!firstText.isEmpty()) mClearTextButton.setVisibility(View.VISIBLE);
        else if (firstText.isEmpty()) mClearTextButton.setVisibility(View.INVISIBLE);
    }

    public void textNotEmptyAction() {
        mTranslatedTextScrollView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_element));
        mTranslatedTextScrollView.setVisibility(View.VISIBLE);
        mClearTextButton.setVisibility(View.VISIBLE);
    }

    public void textEmptyAction() {
        mTranslatedTextScrollView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.hide_element));
        mTranslatedTextScrollView.setVisibility(View.INVISIBLE);
        mClearTextButton.setVisibility(View.INVISIBLE);
    }

    public void swapLanguages() {
        String buffer = LanguagesManager.getFirstLanguage();
        LanguagesManager.setFirstLanguage(LanguagesManager.getSecondLanguage());
        LanguagesManager.setSecondLanguage(buffer);
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
        mFirstLanguageButton.setText(LanguagesManager.getFirstLanguage());
        mSecondLanguageButton.setText(LanguagesManager.getSecondLanguage());
        onTextChangedAction(mInputText.getText().toString());
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
                    LanguagesManager.setFirstLanguage(data.getStringExtra("LANGUAGE"));
                    updateUI();
                    break;
                case 2:
                    LanguagesManager.setSecondLanguage(data.getStringExtra("LANGUAGE"));
                    updateUI();
                    break;
                case 3:
                    HistoryManager.updateHistory((List<HistoryElement>) data.getSerializableExtra("NEW_HISTORY"));
                    if (data.getStringExtra("TEXT_TO_TRANSLATE") != null) {
                        mFirstLanguageButton.setText(LanguagesManager.getLanguage(data.getStringExtra("FIRST_LANGUAGE")));
                        mSecondLanguageButton.setText(LanguagesManager.getLanguage(data.getStringExtra("SECOND_LANGUAGE")));
                        mInputText.setText(data.getStringExtra("TEXT_TO_TRANSLATE"));
                    }
                    break;
                case 4:
                    HistoryManager.setHistoryElements((List<HistoryElement>) data.getSerializableExtra("NEW_HISTORY"));
                    if (data.getStringExtra("TEXT_TO_TRANSLATE") != null) {
                        mFirstLanguageButton.setText(LanguagesManager.getLanguage(data.getStringExtra("FIRST_LANGUAGE")));
                        mSecondLanguageButton.setText(LanguagesManager.getLanguage(data.getStringExtra("SECOND_LANGUAGE")));
                        mInputText.setText(data.getStringExtra("TEXT_TO_TRANSLATE"));
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
                mUtils.hideKeyboard();
                mSlide.show();
                mRecommendationFloatButton.hide();
                break;
            case R.id.swapLanguage:
                swapLanguages();
                break;
            case R.id.firstLanguage:
                Intent intent1 = new Intent(MainActivity.this, SelectLanguageActivity.class);
                intent1.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) LanguagesManager.getLanguagesList());
                startActivityForResult(intent1, 1);
                break;
            case R.id.secondLanguage:
                Intent intent2 = new Intent(MainActivity.this, SelectLanguageActivity.class);
                intent2.putStringArrayListExtra("LANGUAGES", (ArrayList<String>) LanguagesManager.getLanguagesList());
                startActivityForResult(intent2, 2);
                break;
            case R.id.clearTextMain:
                clearText();
                break;
            case R.id.addToFavoritesButtonMain:
                if (HistoryManager.getCurrentHistoryElement().getFirstText().isEmpty()) return;
                TimerManager.cancelAddHistoryElementTimer();
                if (HistoryManager.getElementInHistoryIndex(HistoryManager.getCurrentHistoryElement()) != 0) {
                    HistoryManager.getCurrentHistoryElement().setFavorite(!HistoryManager.getCurrentHistoryElement().isFavorite());
                    HistoryManager.addHistoryElement(HistoryManager.getCurrentHistoryElement());
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
                bundle.putBoolean("IS_ONLY_FAVORITES", true);
                bundle.putParcelableArrayList("HISTORY", (ArrayList<? extends Parcelable>) HistoryManager.getHistoryElements());
                intent.putExtras(bundle);
                startActivityForResult(intent, 3);
                break;
            case 3:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                bundle.putBoolean("IS_ONLY_FAVORITES", false);
                bundle.putParcelableArrayList("HISTORY", (ArrayList<? extends Parcelable>) HistoryManager.getHistoryElements());
                intent.putExtras(bundle);
                startActivityForResult(intent, 4);
                break;
            case 4:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case 6:
                mUtils.startBrowser("https://github.com/ZZooRM/Translator");
                break;
        }
        return false;
    }
}
