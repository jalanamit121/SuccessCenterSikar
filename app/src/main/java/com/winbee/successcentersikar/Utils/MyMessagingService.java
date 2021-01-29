package com.winbee.successcentersikar.Utils;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.winbee.successcentersikar.activity.MainActivity;
import com.winbee.successcentersikar.R;
import com.winbee.successcentersikar.activity.YouTubeVideoList;


import javax.security.auth.Subject;

public class MyMessagingService extends FirebaseMessagingService {

  
    @Override
    public void onMessageReceived( RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotifications(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        liveNotifications(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        testNotifications(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }


    public void showNotifications(String title , String message){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"Common")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setContentText(message)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(999,builder.build());
    }

    public void liveNotifications(String title , String message){
        Intent intent = new Intent(this, YouTubeVideoList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"liveClass")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setContentText(message)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(999,builder.build());
    }
    public void testNotifications(String title , String message){
        Intent intent = new Intent(this, Subject.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"testSeries")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setContentText(message)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(999,builder.build());
    }
}
