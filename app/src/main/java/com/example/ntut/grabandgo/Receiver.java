package com.example.ntut.grabandgo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Receiver extends BroadcastReceiver {
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private boolean isSaveInService = true;
    private boolean isConn = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {      //當手機開機時...
            Intent serviceIntent = new Intent(context, OrderService.class); //使用Intent
            context.startService(serviceIntent);                            //啟動Service

            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);                          //啟動MainActivity

            //手機是否已連上網路...若無則再開啟一次Service
            connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable() && isConn ==false) {
                serviceIntent = new Intent(context, OrderService.class);
                context.startService(serviceIntent);
            }
        }
    }

    public void setIsConn(Boolean isConn) {
        this.isConn = isConn;
    }
}
