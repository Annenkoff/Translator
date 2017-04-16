package me.annenkov.translator.helper;

import android.view.View;
import android.view.animation.AnimationUtils;

import me.annenkov.translator.R;
import me.annenkov.translator.activity.MainActivity;
import me.annenkov.translator.manager.LanguagesManager;

/**
 * Класс с событиями, которые мешались в Activity.
 */
public class Action {
    public static void textStatusAction(MainActivity activity, String firstText, String secondText, boolean isShowed) {
        if (!firstText.isEmpty() && !secondText.isEmpty() && !isShowed)
            textNotEmptyAction(activity);
        else if (firstText.isEmpty() && secondText.isEmpty() && isShowed) textEmptyAction(activity);
        else if (!firstText.isEmpty()) activity.getClearTextButton().setVisibility(View.VISIBLE);
        else if (firstText.isEmpty()) activity.getClearTextButton().setVisibility(View.INVISIBLE);
    }

    private static void textNotEmptyAction(MainActivity activity) {
        activity.getTranslatedTextScrollView().startAnimation(AnimationUtils.loadAnimation(activity, R.anim.show_element));
        activity.getTranslatedTextScrollView().setVisibility(View.VISIBLE);
        activity.getClearTextButton().setVisibility(View.VISIBLE);
        activity.getVocalizeFirstText().setVisibility(View.VISIBLE);
    }

    private static void textEmptyAction(MainActivity activity) {
        activity.getTranslatedTextScrollView().startAnimation(AnimationUtils.loadAnimation(activity, R.anim.hide_element));
        activity.getTranslatedTextScrollView().setVisibility(View.INVISIBLE);
        activity.getClearTextButton().setVisibility(View.INVISIBLE);
        activity.getVocalizeFirstText().setVisibility(View.INVISIBLE);
    }

    public static void firstLanguageIsRightAction(final MainActivity activity, final String firstText) {
        activity.getRightLanguageButton().setText(activity.getString(R.string.set) + " " + LanguagesManager.getRightLanguage(activity, firstText));
        activity.getRecommendationFloatButton().show();
        activity.getRightLanguageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguagesManager.makeFirstLanguageRight(activity, firstText);
                activity.updateUI();
                activity.getSlide().hide();
                activity.getRecommendationFloatButton().hide();
            }
        });
    }
}
