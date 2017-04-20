package me.annenkov.translator.helper;

import android.view.Gravity;
import android.view.View;

import com.mancj.slideup.SlideUp;

import me.annenkov.translator.R;
import me.annenkov.translator.activity.MainActivity;
import me.annenkov.translator.manager.LanguagesManager;
import me.annenkov.translator.tools.Utils;

public class SliderHelper {
    public SlideUp getSlider(final MainActivity mainActivity) {
        return new SlideUp.Builder(mainActivity.findViewById(R.id.slide_recommendation))
                .withListeners(new SlideUp.Listener() {
                    @Override
                    public void onSlide(float percent) {
                        mainActivity.getDim().setAlpha(1 - (percent / 100));
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE
                                && !mainActivity.getInputText().getText().toString().isEmpty()
                                && !LanguagesManager.isFirstLanguageRight()) {
                            mainActivity.getRecommendationFloatButton().show();
                        } else if (visibility == View.VISIBLE) {
                            Utils.hideKeyboard(mainActivity);
                            mainActivity.getRecommendationFloatButton().hide();
                        }
                    }
                })
                .withStartState(SlideUp.State.HIDDEN)
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .build();
    }
}
