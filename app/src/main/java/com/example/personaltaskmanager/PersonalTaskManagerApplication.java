package com.example.personaltaskmanager;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class PersonalTaskManagerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}

