package com.example.application.emergency.activities.user;

import java.util.Observable;

/**
 * Created by batmaster on 5/16/2017 AD.
 */

public class UserListReloadObservable extends Observable {
    private static UserListReloadObservable instance;

    private UserListReloadObservable() {

    }

    public static UserListReloadObservable getInstance() {
        if (instance == null) {
            instance = new UserListReloadObservable();
        }

        return instance;
    }

    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }
}
