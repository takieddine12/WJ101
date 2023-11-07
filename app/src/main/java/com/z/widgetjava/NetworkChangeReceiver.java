package com.z.widgetjava;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private TelephonyManager telephonyManager;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NetworkService.class);
        if (intent != null && "com.z.widgets.networkChangeReceiver.actionCancelNotification".equals(intent.getAction())) {
            if (context != null) {
                context.stopService(serviceIntent);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (context != null) {
                    context.startForegroundService(serviceIntent);
                }
            } else {
                if (context != null) {
                    context.startActivity(serviceIntent);
                }
            }
        }
    }
}