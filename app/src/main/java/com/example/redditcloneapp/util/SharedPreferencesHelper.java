package com.example.redditcloneapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREF_NAME = "reddit_clone_prefs";
    private static final String KEY_INTRO_SHOWN = "intro_shown";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isIntroShown(Context context) {
        return getPrefs(context).getBoolean(KEY_INTRO_SHOWN, false);
    }

    public static void setIntroShown(Context context, boolean isShown) {
        getPrefs(context)
                .edit()
                .putBoolean(KEY_INTRO_SHOWN, isShown)
                .apply();
    }

}
