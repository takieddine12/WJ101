package com.z.widgetjava;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TelephonyManager telephonyManager;
    private final NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("com.z.widgets.networkChangeReceiver.actionCancelNotification");
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }
}