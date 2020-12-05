package com.muzamilhussain.i170191_i170228;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseAppClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}