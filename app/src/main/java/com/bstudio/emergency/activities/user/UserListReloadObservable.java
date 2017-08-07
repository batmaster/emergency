package com.bstudio.emergency.activities.user;

import java.util.Observable;

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
