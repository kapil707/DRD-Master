package com.drd.drdmaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.nio.channels.AlreadyBoundException;

public class AlaramHandler {

    private Context context;

    public AlaramHandler(Context context) {
        this.context = context;
    }

    //https://www.youtube.com/watch?v=rnTo7Aq8M9k
    //https://www.youtube.com/watch?v=hPHM9e7t-SA

    public void setAlarmManager()
    {
        Intent intent = new Intent(context,ExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,2,intent,0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am!=null){
            long triggerAfter = 2 * 5 * 1000;
            long triggerEvery = 2 * 5 * 1000;
            am.setRepeating(AlarmManager.RTC_WAKEUP,triggerAfter,triggerEvery,sender);
        }

    }

    public void cancelAlarmManager()
    {
        Intent intent = new Intent(context,ExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,2,intent,0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am!=null) {
            am.cancel(sender);
        }
    }
}
