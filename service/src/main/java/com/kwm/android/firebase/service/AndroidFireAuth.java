package com.kwm.android.firebase.service;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import androidx.annotation.NonNull;

public class AndroidFireAuth {

    public void signInWith(String email, String password, final AuthStatusListener signInStatusListener){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                signInStatusListener.onSuccess(authResult);
                            }
                        }
                ).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                signInStatusListener.onFailure(e.getMessage());
                            }
                        }
                );
    }

    public void createAccountWith(String email, String password, final AuthStatusListener signInStatusListener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                signInStatusListener.onSuccess(authResult);
                            }
                        }
                ).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                signInStatusListener.onFailure(e.getMessage());
                            }
                        }
        );
    }

    public void signOut(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
    }

    public void listenToLoginStateChanges(final AuthState authState){
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(
                new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if(firebaseUser != null) {
                            try{
                                authState.whenLoggedIn(firebaseUser);
                                if(firebaseUser.isEmailVerified()) {
                                    authState.whenLoggedInAndEmailVerified(firebaseUser);
                                } else {
                                    authState.whenLoggedInAndEmailNotVerified(firebaseUser);
                                }
                            } catch (Exception err) {}
                        } else if (firebaseUser == null) {
                            authState.whenLoggedOut();
                        }

                        authState.whenChanged(firebaseUser);
                    }
                }
        );
    }

    public void sendVerificationEmail(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null) {
            Log.e("VerificaitonEmail", "No user detected");
            return;
        }
        auth.getCurrentUser().sendEmailVerification();
    }

    public void sendPasswordResetEmail(String email, final AuthStatusListener authStatusListener){
        if(getUser() == null) {
            Log.d("SendPasswordReset", "No user detected");
            return;
        }
        getAuth().sendPasswordResetEmail(email).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        authStatusListener.onSuccess(null);
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        authStatusListener.onFailure(e.getMessage());
                    }
                }
        );
    }

    public void signInAnonymously(final AuthStatusListener authStatusListener){
        getAuth().signInAnonymously().addOnSuccessListener(
                new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        authStatusListener.onSuccess(authResult);
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        authStatusListener.onFailure(e.getMessage());
                    }
                }
        );
    }
    public boolean isSignedIn(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public FirebaseAuth getAuth(){
        return FirebaseAuth.getInstance();
    }

    public boolean isEmailVerified(){
        return getAuth().getCurrentUser().isEmailVerified();
    }

    public FirebaseUser getUser(){
        return getAuth().getCurrentUser();
    }
    public interface AuthStatusListener {
        void onSuccess(AuthResult result);
        void onFailure(String errorMessage);
    }
    public interface AuthState {
        void whenChanged(FirebaseUser user);
        void whenLoggedIn(FirebaseUser user);
        void whenLoggedOut();
        void whenLoggedInAndEmailNotVerified(FirebaseUser user);
        void whenLoggedInAndEmailVerified(FirebaseUser user);
    }



}


