package me.annenkov.translator.helper;

import android.view.Gravity;
import android.view.View;

import com.mancj.slideup.SlideUp;

import me.annenkov.translator.R;
import me.annenkov.translator.activity.MainActivity;
import me.annenkov.translator.manager.LanguagesManager;
import me.annenkov.translator.manager.NetworkManager;

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
                                && !mainActivity.getInputText().getText().toString().isEmpty()) {
                            new NetworkManager.AsyncRequestToGetRightLanguageReduction(new NetworkManager.AsyncRequestToGetRightLanguageReduction.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    if (!output.equalsIgnoreCase(LanguagesManager.getFirstLanguageReduction())) {
                                        mainActivity.getRecommendationFloatButton().show();
                                    }
                                }
                            }).execute(LanguagesManager.getFirstLanguageReduction());
                        }
                    }
                })
                .withStartState(SlideUp.State.HIDDEN)
                .withStartGravity(Gravity.TOP)
                .build();
    }
}
