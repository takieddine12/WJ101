package com.z.widgetjava;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private PowerManager.WakeLock wakeLock;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NetworkService.class);

        if (intent != null && "com.z.widgets.networkChangeReceiver.actionCancelNotification".equals(intent.getAction())) {
            Toast.makeText(context, "Reset Button Clicked", Toast.LENGTH_LONG).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startActivity(serviceIntent);
            }
        }

        // Delayed execution to ensure that the WakeLock is acquired after some time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                wakeDevice(context);
            }
        }, 10000);
    }

    private void wakeDevice(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "MyApp:WakeLock"
        );

        // Turn on the screen for 15 seconds
        turnOnScreenForDuration();
    }

    private void turnOnScreenForDuration() {
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire(15000);

            new Thread(() -> {
                // Release the wakelock after the specified duration
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }).start();
        }
    }
}