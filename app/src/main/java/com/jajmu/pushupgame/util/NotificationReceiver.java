package com.jajmu.pushupgame.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.jajmu.pushupgame.GcmIntentService;
import com.jajmu.pushupgame.R;
import com.jajmu.pushupgame.gameview.GameActivity;

public class NotificationReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 1;

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("received by notification receiver");
        Bundle b = intent.getExtras();
        Intent notificationIntent = new Intent(context, GameActivity.class);
        //if we already have game activity running, clear the stack and show it
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //find the class to show based on the game state
        notificationIntent.putExtras(b);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(b.get("title").toString())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(b.get("message").toString()))
                        .setContentText(b.get("message").toString());

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
