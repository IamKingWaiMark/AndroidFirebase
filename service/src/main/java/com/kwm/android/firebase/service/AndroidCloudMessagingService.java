package com.kwm.android.firebase.service;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AndroidCloudMessagingService extends FirebaseMessagingService {
    private boolean autoNotification = false;
    private OnMessageReceived onMessageReceived;
    private int notificationId = 0;

    public AndroidCloudMessagingService(){
        super();
    }
    /**
     * Subscribe to an topic to receive notification for a specific topic.
     * @param topic Topic to subscribe to
     * @param subscribeListener Use this to handle what happens when the attemp to subscribe succeeds or fail.
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

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(this.autoNotification) {
            this.sendNotification(remoteMessage);
        }

        try{
            this.onMessageReceived.onReceived(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } catch (Exception err) {
            Log.e("AndroidCloudMessagingService", err.getMessage());
        }
    }

    private void sendNotification(@NonNull RemoteMessage remoteMessage){
        try {
            String channelId = "AndroidCloudMessagingService" + notificationId;

            createNotificationChannel(channelId);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(remoteMessage.getNotification().getBody()))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.very_small_fb_icon);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(notificationId++, builder.build());
        } catch (Exception err) {
            Log.e("AndroidCloudMessagingService", err.getMessage() + "");
        }
    }

    private void createNotificationChannel(String notificationChannel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AndroidCloudMessagingService";
            String description = "AndroidCloudMessagingService App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(notificationChannel, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void setOnMessageReceived(OnMessageReceived onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    /**
     * Turns on auto notifications. When a new Firebase Cloud Message is sent, a notification will also be provoked.
     */
    public void autoNotification(){
        this.autoNotification = true;
    }

    /**
     * Stops the auto notification process. By default, auto notification is turned off.
     */
    public void stopAutoNotification(){
        this.autoNotification = false;
    }


    public interface SubscribeListener {
        void onSuccess();
        void onFail();
    }
    public interface OnMessageReceived {
        void onReceived(String title, String body);
    }

}
