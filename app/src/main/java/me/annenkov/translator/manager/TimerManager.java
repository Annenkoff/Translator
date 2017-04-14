package me.annenkov.translator.manager;

import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;

import me.annenkov.translator.activity.MainActivity;
import me.annenkov.translator.model.HistoryElement;

public class TimerManager {
    private static Timer sAddHistoryElementTimer;
    private static Timer sTranslateTimer;

    public static Timer getAddHistoryElementTimer() {
        return sAddHistoryElementTimer;
    }

    public static void cancelAddHistoryElementTimer() {
        sAddHistoryElementTimer.cancel();
    }

    public static Timer getTranslateTimer() {
        return sTranslateTimer;
    }

    public static void cancelTranslateTimer() {
        sTranslateTimer.cancel();
    }

    public static void startAddHistoryElementTimer(HistoryElement historyElement, long delay) {
        sAddHistoryElementTimer = new Timer(true);
        sAddHistoryElementTimer.schedule(new TimerTaskToAddHistoryElement(historyElement), delay);
    }

    public static void startTranslateTimer(final Activity activity, String notTranslatedText, long delay) {
        sTranslateTimer = new Timer(true);
        sTranslateTimer.schedule(new TranslateTimer(activity, notTranslatedText), delay);
    }

    private static class TimerTaskToAddHistoryElement extends java.util.TimerTask {
        private HistoryElement mHistoryElement;

        TimerTaskToAddHistoryElement(HistoryElement historyElement) {
            mHistoryElement = historyElement;
        }

        @Override
        public void run() {
            HistoryManager.addHistoryElement(mHistoryElement);
        }
    }

    private static class TranslateTimer extends TimerTask {
        private MainActivity mActivity;
        private String mNotTranslatedText;

        public TranslateTimer(Activity activity, String notTranslatedText) {
            mActivity = (MainActivity) activity;
            mNotTranslatedText = notTranslatedText;
        }

        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.onTextChangedAction(mNotTranslatedText);
                }
            });
        }
    }
}
