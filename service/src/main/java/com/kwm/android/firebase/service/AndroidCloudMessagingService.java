package com.kwm.android.firebase.service;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


import androidx.annotation.NonNull;

public class AndroidCloudMessagingService extends FirebaseMessagingService {
    /**
     * Subscribe to an topic to receive notification for a specific topic.
     * @param topic
     */
    public static void subscribeTo(String topic, final SubscribeListener subscribeListener){
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        subscribeListener.onSuccess();
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        subscribeListener.onFail();
                    }
                }
        );
    }


    public interface SubscribeListener {
        void onSuccess();
        void onFail();
    }
}
