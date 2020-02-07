package com.kwm.android.firebase.service;

import android.content.Context;

import com.google.firebase.FirebaseApp;

/**
 * For this library,
 * 1. Need implement'com.google.firebase:firebase-auth:x.x.x'
 *
 * Usage
 * 1. In the project level Gradle file, must add the  classpath 'com.google.gms:google-services:4.3.3' dependency
 * 2. Must apply plugin: 'com.google.gms.google-services' at the bottom of the App level Gradle
 */
public class AndroidFirebaseApp {

    public static void init(Context context){
        FirebaseApp.initializeApp(context);
    }
    public static FirebaseApp getInstance(){
        return FirebaseApp.getInstance();
    }
}
