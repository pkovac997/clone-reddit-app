package com.example.redditcloneapp.infrastructure.local.sharedPrefeences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.redditcloneapp.infrastructure.local.constant.LocalDbConstants;

public class SharedPreferencesHelper {

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(LocalDbConstants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isIntroShown(Context context) {
        return getPrefs(context).getBoolean(LocalDbConstants.KEY_INTRO_SHOWN, false);
    }

    public static void setIntroShown(Context context, boolean isShown) {
        getPrefs(context)
                .edit()
                .putBoolean(LocalDbConstants.KEY_INTRO_SHOWN, isShown)
                .apply();
    }
}
