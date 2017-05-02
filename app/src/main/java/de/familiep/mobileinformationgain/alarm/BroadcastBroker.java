package de.familiep.mobileinformationgain.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastBroker extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch(intent.getAction()){
            case Intent.ACTION_BOOT_COMPLETED:
                context.startService(new Intent(context, DataCollectionAlarmIntent.class));
                break;
        }
    }

}
