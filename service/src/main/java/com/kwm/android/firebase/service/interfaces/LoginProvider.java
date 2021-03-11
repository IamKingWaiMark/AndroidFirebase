package com.kwm.android.firebase.service.interfaces;

public enum LoginProvider {


    github("github.com"),
    yahoo("yahoo.com"),
    google("google.com"),
    twitter("twitter.com"),
    apple("apple.com"),
    microsoft("microsoft.com");

    private String providerString = "";
    LoginProvider(String providerString) {
        this.providerString = providerString;
    }

    public String getProviderString() {
        return providerString;
    }

}
