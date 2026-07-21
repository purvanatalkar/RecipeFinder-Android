package com.example.recipefinder.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "RecipeAppSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    // Context वाला Constructor जिसे आपकी LoginActivity ढूंढ रही है
    public SessionManager(Context context) {
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = pref.edit();
    }

    // setLogin मेथड ताकि एरर दूर हो सके
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // isLoggedIn मेथड जो SplashActivity में काम आएगा
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // यूज़र लॉगआउट के लिए
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}