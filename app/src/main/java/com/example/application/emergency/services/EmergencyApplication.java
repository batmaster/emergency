package com.example.application.emergency.services;

import android.app.Application;

public class EmergencyApplication extends Application {

    private HTTPService httpService;
    private Preferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        httpService = new HTTPService(this);
        preferences = new Preferences(this);

    }

    public HTTPService getHttpService() {
        return httpService;
    }

    public Preferences getPreferences() {
        return preferences;
    }
}
