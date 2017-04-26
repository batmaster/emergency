package com.example.application.emergency.services;

import android.app.Application;
import android.telephony.TelephonyManager;

public class EmergencyApplication extends Application {

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

    public HTTPService getHttpService() {
        return httpService;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public String getPhoneNumber() {
        return tm.getLine1Number();
    }

    public String getUUID() {
        return tm.getDeviceId();
    }
}
