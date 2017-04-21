package me.annenkov.translator.tools;

import android.view.View;
import android.view.animation.AnimationUtils;

import me.annenkov.translator.R;
import me.annenkov.translator.activity.MainActivity;
import me.annenkov.translator.manager.CacheManager;
import me.annenkov.translator.manager.HistoryManager;
import me.annenkov.translator.manager.LanguagesManager;
import me.annenkov.translator.manager.NetworkManager;
import me.annenkov.translator.model.HistoryElement;

/**
 * Класс с объёмными методами, которые
 * отвечают за основные события в приложении.
 */
public class Action {
    private static void textStatusAction(MainActivity mainActivity, String firstText, boolean isShowed) {
        if (!firstText.isEmpty() && !isShowed)
            textNotEmptyAction(mainActivity);
        else if (firstText.isEmpty() && isShowed) textEmptyAction(mainActivity);
    }

    private static void textNotEmptyAction(MainActivity mainActivity) {
        mainActivity.getTranslatedTextScrollView().startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.show_element));
        mainActivity.getTranslatedTextScrollView().setVisibility(View.VISIBLE);
        mainActivity.getVocalizeFirstText().setVisibility(View.VISIBLE);
    }

    public static void textEmptyAction(MainActivity mainActivity) {
        mainActivity.getTranslatedTextScrollView().startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.hide_element));
        mainActivity.getTranslatedTextScrollView().setVisibility(View.INVISIBLE);
        mainActivity.getVocalizeFirstText().setVisibility(View.INVISIBLE);
        LanguagesManager.setIsFirstLanguageRight(true);
        mainActivity.getRecommendationFloatButton().hide();
    }

    private static void firstLanguageIsNotRightAction(final MainActivity activity, final String rightLanguage) {
        activity.getRightLanguageButton().setText(activity.getString(R.string.set) + " " + rightLanguage);
        LanguagesManager.setIsFirstLanguageRight(false);
        activity.getRecommendationFloatButton().show();
        activity.getRightLanguageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguagesManager.setFirstLanguage(rightLanguage);
                LanguagesManager.setIsFirstLanguageRight(true);
                activity.updateUI();
                activity.getSlide().hide();
            }
        });
    }

    public static void onTextChangedAction(final MainActivity mainActivity, final String firstText) {
        mainActivity.getRecommendationFloatButton().hide();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                translateAction(mainActivity, firstText);
                rightLanguageAction(mainActivity, firstText);
            }
        });
    }

    private static void translateAction(final MainActivity mainActivity, final String firstText) {
        mainActivity.getTranslatedText().setText("...");
        textStatusAction(mainActivity, firstText, mainActivity.getTranslatedTextScrollView().getVisibility() == View.VISIBLE);
        String secondText = CacheManager
                .getTranslationFromText(LanguagesManager.getFirstLanguageReduction(), firstText);
        if (secondText == null) {
            new NetworkManager.AsyncRequestToGetTranslatedText(mainActivity, new NetworkManager.AsyncRequestToGetTranslatedText.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    mainActivity.getTranslatedText().setText(output);
                    if (mainActivity.getString(R.string.network_error).equals(output)) return;
                    addInHistoryAction(mainActivity, output);
                }
            }).execute(firstText, LanguagesManager.getFirstLanguageReduction(), LanguagesManager.getSecondLanguageReduction());
        } else mainActivity.getTranslatedText().setText(secondText);
    }

    private static void addInHistoryAction(MainActivity mainActivity, String output) {
        HistoryElement historyElement = HistoryManager.getCurrentHistoryElement(mainActivity);
        if (!output.isEmpty()
                && !HistoryManager.getFirstHistoryElement().equals(historyElement))
            HistoryManager.addHistoryElementWithTimer(HistoryManager.getCurrentHistoryElement(mainActivity));
    }

    private static void rightLanguageAction(final MainActivity mainActivity, final String firstText) {
        new NetworkManager.AsyncRequestToGetRightLanguageReduction(new NetworkManager.AsyncRequestToGetRightLanguageReduction.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (!LanguagesManager.getFirstLanguageReduction().equalsIgnoreCase(output) && !output.isEmpty())
                    firstLanguageIsNotRightAction(mainActivity, LanguagesManager.getLanguage(output));
                else LanguagesManager.setIsFirstLanguageRight(true);
            }
        }).execute(firstText);
    }
}
