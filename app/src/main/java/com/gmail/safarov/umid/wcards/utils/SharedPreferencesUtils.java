package com.gmail.safarov.umid.wcards.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class SharedPreferencesUtils {
    private static final String PREFERENCES_NAME = "WCARDS_PREFERENCES";
    private static final String KEY_FIRST_RUN = "KEY_FIRST_RUN";

    public static boolean isFirstRun(@NonNull Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        boolean result = preferences.getBoolean(KEY_FIRST_RUN, true);

        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(KEY_FIRST_RUN, false);
        edit.apply();

        return result;
    }
}
