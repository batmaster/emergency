package com.example.application.emergency.services;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by batmaster on 2/26/16 AD.
 */
public class Preferences {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static final String KEY_OFFICER_ID = "KEY_OFFICER_ID";

    public Preferences(Context context) {
        sp = context.getSharedPreferences("emergency", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public void removeString(String key) {
        editor.remove(key);
        editor.commit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }


}
