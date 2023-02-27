package com.drd.drdmaster;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.legacy.content.WakefulBroadcastReceiver;


public class MyWakefulReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
        /*Intent service = new Intent(context, MyIntentService.class);
        startWakefulService(context, service);*/
    }
}
