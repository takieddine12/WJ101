package com.z.widgetjava;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class NetworkService extends android.app.Service {

    private NotificationManager notificationManager;


    @Override
    public void onCreate() {
        super.onCreate();
        showNetworkChangeNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void showNetworkChangeNotification() {

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("network_change_channel", "Network Change Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.custom_ui_layout);
        remoteViews.setTextViewText(R.id.text,"This is a testing text");

        
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        @SuppressLint("NotificationTrampoline") NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "network_change_channel")
                .setContentTitle("Notification")
                .setContentText("Showing simple notification")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentIntent(pendingIntent)
                .setContent(remoteViews)
                .addAction(R.drawable.baseline_circle_notifications_24, "Reset", getCancelPendingIntent())
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, notificationBuilder.build());
        } else {
            notificationManager.notify(1, notificationBuilder.build());
        }



    }

    private PendingIntent getCancelPendingIntent() {
        Intent intent = new Intent(this, NetworkChangeReceiver.class);
        intent.setAction("com.z.widgets.networkChangeReceiver.actionCancelNotification");
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(1);
        stopSelf();
    }
}