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

import androidx.core.app.NotificationCompat;

public class NetworkService extends android.app.Service {

    private TelephonyManager telephonyManager;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onDataConnectionStateChanged(int state, int networkType) {
                String networkTypeString = getNetworkType(networkType);
                showNetworkChangeNotification(networkTypeString);
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getNetworkType(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_GSM:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            default:
                return "Unknown";
        }
    }

    private void showNetworkChangeNotification(String networkType) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("network_change_channel", "Network Change Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "network_change_channel")
                .setContentTitle("Network Changed")
                .setContentText("Network Type: " + networkType)
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.baseline_circle_notifications_24, "Reset", getCancelPendingIntent())
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, notificationBuilder.build());
        } else {
            notificationManager.notify(1, notificationBuilder.build());
        }

        Log.d("TAG", "Current Rsrp " + getRsrpValues());

        if (getRsrpValues() >= -100 && getRsrpValues() <= -120) {
            Log.d("TAG", "RSRP " + getRsrpValues());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notificationManager.cancel(1);
                }
            }, 15000);
        }
    }

    private PendingIntent getCancelPendingIntent() {
        Intent intent = new Intent(this, NetworkChangeReceiver.class);
        intent.setAction("com.z.widgets.networkChangeReceiver.actionCancelNotification");
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    private int getRsrpValues() {
        int rsrp = 0;
        for (CellInfo cellInfo : telephonyManager.getAllCellInfo()) {
            if (cellInfo instanceof CellInfoLte) {
                rsrp = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp();
            }
        }
        return rsrp;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(1);
        stopSelf();
    }
}