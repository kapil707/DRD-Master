package com.drd.drdmaster;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

public class MyIntentService extends JobService {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MyIntentService() {
        super();
        //startForegroundService(new Intent(getApplicationContext(),MyforgoundServies.class));
        startService(new Intent(getApplicationContext(),Mybackground.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //startForegroundService(new Intent(getApplicationContext(),MyforgoundServies.class));
        startService(new Intent(getApplicationContext(),Mybackground.class));
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        //startForegroundService(new Intent(getApplicationContext(),MyforgoundServies.class));
        startService(new Intent(getApplicationContext(),Mybackground.class));
        return false;
    }

    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        // Do the work that requires your app to keep the CPU running.
        // ...
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        MyWakefulReceiver.completeWakefulIntent(intent);
    }
}
