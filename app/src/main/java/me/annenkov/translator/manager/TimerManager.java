package me.annenkov.translator.manager;

import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;

import me.annenkov.translator.activity.MainActivity;
import me.annenkov.translator.model.HistoryElement;
import me.annenkov.translator.tools.Action;

/**
 * Класс для работы с таймером.
 * Используется два разных таймера для перевода и
 * добавления в историю. Сделано для того, чтобы
 * в историю не добавлялись лишние "промежуточные"
 * элементы. Таким образом, перевод происходит быстро,
 * а заспамливания истории почти не происходит.
 */
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

    /**
     * Откладывает добавление элемента в историю.
     *
     * @param historyElement Элемент, который добавляется в историю.
     */
    public static void startAddHistoryElementTimer(HistoryElement historyElement) {
        sAddHistoryElementTimer = new Timer(true);
        sAddHistoryElementTimer.schedule(new TimerTaskToAddHistoryElement(historyElement), 1700);
    }

    /**
     * Откладывает перевод на определённое время, чтобы не происходило
     * заспамливания и слишком большой очереди.
     *
     * @param activity  Активити, для выполнения runOnUiThread.
     * @param firstText Ещё не переведённый текст.
     */
    public static void startTranslateTimer(final Activity activity, String firstText) {
        sTranslateTimer = new Timer(true);
        sTranslateTimer.schedule(new TranslateTimer(activity, firstText), 500);
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
                    Action.onTextChangedAction(mActivity, mNotTranslatedText);
                }
            });
        }
    }
}
