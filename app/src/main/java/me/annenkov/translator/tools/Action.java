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
    private static void textStatusAction(MainActivity activity, String firstText, boolean isShowed) {
        if (!firstText.isEmpty() && !isShowed)
            textNotEmptyAction(activity);
        else if (firstText.isEmpty() && isShowed) textEmptyAction(activity);
    }

    private static void textNotEmptyAction(MainActivity activity) {
        activity.getTranslatedTextScrollView().startAnimation(AnimationUtils.loadAnimation(activity, R.anim.show_element));
        activity.getTranslatedTextScrollView().setVisibility(View.VISIBLE);
        activity.getVocalizeFirstText().setVisibility(View.VISIBLE);
    }

    public static void textEmptyAction(MainActivity activity) {
        activity.getTranslatedTextScrollView().startAnimation(AnimationUtils.loadAnimation(activity, R.anim.hide_element));
        activity.getTranslatedTextScrollView().setVisibility(View.INVISIBLE);
        activity.getVocalizeFirstText().setVisibility(View.INVISIBLE);
        activity.getRecommendationFloatButton().hide();
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
        translateAction(mainActivity, firstText);
        rightLanguageAction(mainActivity, firstText);
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
                String rightLanguageReduction = output;
                if (!LanguagesManager.isLanguageExists(rightLanguageReduction) && !rightLanguageReduction.isEmpty())
                    rightLanguageReduction = "en";
                if (!LanguagesManager.getFirstLanguageReduction().equalsIgnoreCase(rightLanguageReduction) && !rightLanguageReduction.isEmpty())
                    firstLanguageIsNotRightAction(mainActivity, LanguagesManager.getLanguage(rightLanguageReduction));
                else LanguagesManager.setIsFirstLanguageRight(true);
            }
        }).execute(firstText);
    }
}
