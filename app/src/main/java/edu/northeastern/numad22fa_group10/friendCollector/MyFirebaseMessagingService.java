package edu.northeastern.numad22fa_group10.friendCollector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import edu.northeastern.numad22fa_group10.MainActivity;
import edu.northeastern.numad22fa_group10.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String CHANNEL_ID  = "CHANNEL_ID";
    private static final String CHANNEL_NAME  = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION  = "CHANNEL_DESCRIPTION";

    public MyFirebaseMessagingService() {
        super();
        Log.d(TAG,"FCMService running");
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);
        Log.d(TAG, "Refreshed token: " + newToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        myClassifier(remoteMessage);

        Log.e("msgId", remoteMessage.getMessageId());
        Log.e("senderId", remoteMessage.getSenderId());
    }

    // [END receive_message]

    private void myClassifier(RemoteMessage remoteMessage) {

        String identificator = remoteMessage.getFrom();
        if (identificator != null) {
            if (remoteMessage.getNotification() != null) {
                RemoteMessage.Notification notification = remoteMessage.getNotification();
                showNotification(remoteMessage.getNotification());
//                 postToastMessage(notification.getTitle(), getApplicationContext());
            }
        } else {
            if (remoteMessage.getData().size() > 0) {
                RemoteMessage.Notification notification = remoteMessage.getNotification();
                assert notification != null;
                showNotification(notification);
//                 postToastMessage(remoteMessage.getData().get("title"), getApplicationContext());
            }
        }
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessageNotification FCM message  received.
     */
    private void showNotification(RemoteMessage.Notification remoteMessageNotification) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        } else {
            builder = new NotificationCompat.Builder(this);
        }

        System.out.println("Body is coming" + remoteMessageNotification.getBody());
        Resources resources = getResources();
        int largeIconResourceId = resources.getIdentifier(remoteMessageNotification.getBody(), "drawable", getPackageName());

        notification = builder.setContentTitle(remoteMessageNotification.getTitle())
                .setContentText(remoteMessageNotification.getBody())
                .setSmallIcon(R.drawable.ic_stat_msg)
                .setLargeIcon(BitmapFactory.decodeResource(resources, largeIconResourceId))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0, notification);

    }

    public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void extractPayloadDataForegroundCase(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            postToastMessage(remoteMessage.getData().get("title"));
        }
    }


}
