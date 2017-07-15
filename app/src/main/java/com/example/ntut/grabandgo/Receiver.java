package com.example.ntut.grabandgo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {      //當手機開機時...
            Intent serviceIntent = new Intent(context, OrderService.class); //使用Intent
            context.startService(serviceIntent);                            //啟動Service

            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);                          //啟動MainActivity
        }
    }
}
