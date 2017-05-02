package de.familiep.mobileinformationgain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOnReceiver extends BroadcastReceiver {

    private ScreenEventObservable observable;

    public ScreenOnReceiver(ScreenEventObservable observable) {
        this.observable = observable;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            observable.onScreenOff();
        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            observable.onScreenOn();
        }
        else if(intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            observable.onScreenOff();
        }
    }
}
