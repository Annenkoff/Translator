package me.annenkov.translator.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;

/**
 * Класс с утилитами, которые мешались в Activity.
 */
public class Utils {
    public static void startBrowser(Activity activity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
