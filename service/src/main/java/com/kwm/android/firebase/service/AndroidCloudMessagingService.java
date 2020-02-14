package com.kwm.android.firebase.service;


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

    public AndroidCloudMessagingService(){
        super();
    }
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

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(this.autoNotification) {
            try{
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, remoteMessage.getNotification().getChannelId())
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(remoteMessage.getNotification().getBody()))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(Integer.parseInt(remoteMessage.getMessageId()), builder.build());
            } catch (Exception err) {}
        }

        this.onMessageReceived.onReceived(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }
    public void setOnMessageReceived(OnMessageReceived onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public void autoNotification(){
        this.autoNotification = true;
    }
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
