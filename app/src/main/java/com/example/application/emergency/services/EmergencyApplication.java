package com.example.application.emergency.services;

import android.app.Application;
import android.telephony.TelephonyManager;

/**
 * class จากระบบแอนดรอยด์ สำหรับบรรจุตัวแปรที่ใช้บ่อยในแอปพลิเคชั่น
 */
public class EmergencyApplication extends Application {

    /** ประกาศตัวแปร **/
    private HTTPService httpService;
    private Preferences preferences;

    private TelephonyManager tm;

    @Override
    public void onCreate() {
        super.onCreate();

        httpService = new HTTPService(this);
        preferences = new Preferences(this);

        tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
    }

    /** ฟังก์ชั่นสำหรับเรียกใช้ตัวแปรใน class **/
    public HTTPService getHttpService() {
        return httpService;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public String getPhoneNumber() {
        return tm.getLine1Number();
    }
}
