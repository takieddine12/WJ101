package com.z.widgetjava;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {


    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NetworkService.class);
        if (intent != null && "com.z.widgets.networkChangeReceiver.actionCancelNotification".equals(intent.getAction())) {
            Toast.makeText(context,"Reset Button Clicked",Toast.LENGTH_LONG).show();
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