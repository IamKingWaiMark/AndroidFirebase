package com.kwm.android.firebase.service;

import android.content.Context;

import com.google.firebase.FirebaseApp;

public class AndroidFirebaseApp {

    public static void init(Context context){
        FirebaseApp.initializeApp(context);
    }
}
