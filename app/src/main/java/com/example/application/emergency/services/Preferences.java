package com.example.application.emergency.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 * class สำหรับฟังก์ชั่นที่ใช้บันทึกค่า
 */
public class Preferences {

    /** ประกาศตัวแปร **/
    public static final String KEY_OFFICER_ID = "KEY_OFFICER_ID";
    public static final String KEY_PHONE = "KEY_PHONE";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public Preferences(Context context) {
        sp = context.getSharedPreferences("emergency", Context.MODE_PRIVATE);
        editor = sp.edit();

        Map<String, ?> keys = sp.getAll();

        Log.d("Preferences", "== start ==");
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("Preferences", entry.getKey() + ": " + entry.getValue().toString());
        }
        Log.d("Preferences", "== stop ==");
    }

    /** ฟังก์ชั่นสำหรับอ่านค่าที่บันทึก **/
    public String getString(String key) {
        return sp.getString(key, null);
    }

    /** ฟังก์ชั่นสำหรับลบค่าที่บันทึก **/
    public void removeString(String key) {
        editor.remove(key);
        editor.commit();
    }

    /** ฟังก์ชั่นสำหรับเขียนค่าที่บันทึก **/
    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }


}
