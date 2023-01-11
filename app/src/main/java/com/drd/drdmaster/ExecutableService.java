package com.drd.drdmaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ExecutableService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Working good",Toast.LENGTH_LONG).show();

        /*Intent ii = new Intent(context, MainActivity.class);
        context.startService(ii);*/
    }
}
