package me.annenkov.translator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;

public class Utils {
    private Activity mActivity;

    public Utils(Activity activity) {
        mActivity = activity;
    }

    public void startBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mActivity.startActivity(browserIntent);
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
    }
}
