package me.annenkov.translator.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.view.inputmethod.InputMethodManager;
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
import java.util.Timer;
import java.util.TimerTask;

import me.annenkov.translator.R;
import me.annenkov.translator.manager.HistoryManager;
import me.annenkov.translator.manager.LanguagesManager;
import me.annenkov.translator.manager.NetworkManager;
import me.annenkov.translator.model.HistoryElement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Drawer.OnDrawerListener, Drawer.OnDrawerItemClickListener {
    private View mDim;

    private Toolbar mToolbar;
    private Drawer mDrawer;
    private SlideUp mSlide;

    private Timer mTimer;
    private HistoryManager mHistoryManager;
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
                .withOnDrawerListener(this)
                .withOnDrawerItemClickListener(this)
                .build();
        View view = findViewById(R.id.slide_recommendation);
        mSlide = new SlideUp.Builder(view)
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

        mHistoryManager = new HistoryManager();
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

        mFirstLanguageButton.setText(mLanguagesManager.getFirstLanguage());
        mSecondLanguageButton.setText(mLanguagesManager.getSecondLanguage());

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecommendationFloatButton.hide();
                if (mTimer != null) mTimer.cancel();
                if (mHistoryManager.getTimer() != null) mHistoryManager.getTimer().cancel();
                if (s.length() == 0) {
                    onTextChangedAction(s.toString());
                    return;
                }
                mTimer = new Timer(true);
                mTimer.schedule(new TranslateTimer(s.toString()), 500);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

    private void onTextChangedAction(final String s) {
        if (mHistoryManager.getTimer() != null) mHistoryManager.cancelTimer();
        offAddToFavoritesButton();
        textStatusAction(s, mTranslatedTextScrollView.getVisibility() == View.VISIBLE);
        String request = new NetworkManager(MainActivity.this,
                s,
                mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getFirstLanguage()),
                mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getSecondLanguage())).getTranslatedText();
        mTranslatedText.setText(request);
        HistoryElement historyElement = new HistoryElement(mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getFirstLanguage()).toUpperCase(),
                mLanguagesManager.getLanguageReductions().get(mLanguagesManager.getSecondLanguage()).toUpperCase(),
                s,
                request);
        mHistoryManager.setCurrentHistoryElement(historyElement);
        if (((!request.equals("") || !request.isEmpty())) && !mHistoryManager.getFirstHistoryElement().equals(historyElement)) {
            mHistoryManager.addHistoryElementWithTimer(mHistoryManager.getCurrentHistoryElement(), 1650);
        }
        if (!mLanguagesManager.isFirstLanguageIsRight(s)) {
            final String rightLanguage = mLanguagesManager.getRightLanguage(s);
            mRightLanguageButton.setText(getString(R.string.set) + " " + rightLanguage);
            mRecommendationFloatButton.show();
            mRightLanguageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLanguagesManager.makeFirstLanguageRight(s);
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

    public void textStatusAction(String text, boolean isShowed) {
        if ((!text.isEmpty() || !text.equals("")) && !isShowed) textNotEmptyAction();
        else if ((text.isEmpty() || text.equals("")) && isShowed) textEmptyAction();
    }

    public void textNotEmptyAction() {
        mTranslatedTextScrollView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_element));
        mClearTextButton.setVisibility(View.VISIBLE);
        mTranslatedTextScrollView.setVisibility(View.VISIBLE);
    }

    public void textEmptyAction() {
        mTranslatedTextScrollView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.hide_element));
        mClearTextButton.setVisibility(View.INVISIBLE);
        mTranslatedTextScrollView.setVisibility(View.INVISIBLE);
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
                    mLanguagesManager.setFirstLanguage(data.getStringExtra("LANGUAGE"));
                    updateUI();
                    break;
                case 2:
                    mLanguagesManager.setSecondLanguage(data.getStringExtra("LANGUAGE"));
                    updateUI();
                    break;
                case 3:
                    mHistoryManager.updateHistory((List<HistoryElement>) data.getSerializableExtra("NEW_HISTORY"));
                    if (data.getStringExtra("TEXT_TO_TRANSLATE") != null) {
                        mFirstLanguageButton.setText(mLanguagesManager.getLanguageFromLanguageReduction(data.getStringExtra("FIRST_LANGUAGE")));
                        mSecondLanguageButton.setText(mLanguagesManager.getLanguageFromLanguageReduction(data.getStringExtra("SECOND_LANGUAGE")));
                        mInputText.setText(data.getStringExtra("TEXT_TO_TRANSLATE"));
                    }
                    break;
                case 4:
                    mHistoryManager.setHistoryElements((List<HistoryElement>) data.getSerializableExtra("NEW_HISTORY"));
                    if (data.getStringExtra("TEXT_TO_TRANSLATE") != null) {
                        mFirstLanguageButton.setText(mLanguagesManager.getLanguageFromLanguageReduction(data.getStringExtra("FIRST_LANGUAGE")));
                        mSecondLanguageButton.setText(mLanguagesManager.getLanguageFromLanguageReduction(data.getStringExtra("SECOND_LANGUAGE")));
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
                hideKeyboard();
                mSlide.show();
                mRecommendationFloatButton.hide();
                break;
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

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        hideKeyboard();
        if (mSlide.isVisible()) mSlide.hide();
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        Intent intent;
        Bundle bundle = new Bundle();
        switch (position) {
            case 2:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                bundle.putBoolean("IS_ONLY_FAVORITES", true);
                bundle.putParcelableArrayList("HISTORY", (ArrayList<? extends Parcelable>) mHistoryManager.getHistoryElements());
                intent.putExtras(bundle);
                startActivityForResult(intent, 3);
                break;
            case 3:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                bundle.putBoolean("IS_ONLY_FAVORITES", false);
                bundle.putParcelableArrayList("HISTORY", (ArrayList<? extends Parcelable>) mHistoryManager.getHistoryElements());
                intent.putExtras(bundle);
                startActivityForResult(intent, 4);
                break;
            case 4:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case 6:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/annenkoff/Translator"));
                startActivity(browserIntent);
                break;
        }
        return false;
    }

    private class TranslateTimer extends TimerTask {
        private String mNotTranslatedText;

        TranslateTimer(String notTranslatedText) {
            mNotTranslatedText = notTranslatedText;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onTextChangedAction(mNotTranslatedText);
                }
            });
        }
    }
}
