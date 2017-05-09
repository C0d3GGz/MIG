package de.familiep.mobileinformationgain;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;

import java.util.Calendar;
import java.util.List;

import de.familiep.mobileinformationgain.alarm.DataCollectionAlarmIntent;
import de.familiep.mobileinformationgain.queue.InformationData;
import de.familiep.mobileinformationgain.queue.InformationQueue;
import de.familiep.mobileinformationgain.utils.SharedprefsHelper;

public class CustomAccessibilityService extends AccessibilityService implements ScreenEventObservable {

    private final String TAG = CustomAccessibilityService.class.getName();
    private ScreenOnReceiver screenOnReceiver;
    private InformationQueue queue;
    private boolean isScreenOn;

    @Override
    protected void onServiceConnected() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SHUTDOWN);
        screenOnReceiver = new ScreenOnReceiver(this);
        registerReceiver(screenOnReceiver, intentFilter);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        queue = new InformationQueue(wakeLock, getApplicationContext());

        SharedprefsHelper sharedprefsHelper = new SharedprefsHelper(this);
        if(sharedprefsHelper.getDataCollectionFinishedMillis() == 0L){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());

            if(BuildConfig.DEBUG)
                cal.add(Calendar.SECOND, 10);

            cal.add(Calendar.DAY_OF_YEAR, BuildConfig.INQUIRY_DUR);

            sharedprefsHelper.setDataCollectionFinishedMillis(cal.getTimeInMillis());

            //setup alarm
            startService(new Intent(this, DataCollectionAlarmIntent.class));

        }

        //service could be restarted while it is unknown whether the screen is on or off:
        isScreenOn = isScreenCurrentlyOn();

        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        long now = System.currentTimeMillis();
        if(!isScreenOn) return; //TODO: optimize

        //filter windows_changed event type (package name equals null, content redundant anyway)
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOWS_CHANGED) return;
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return; //fixes #10

        String packageName = null;
        CharSequence sequence = event.getPackageName();
        if(sequence != null){
            packageName = event.getPackageName().toString();
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {

            Parcelable parcelable = event.getParcelableData();
            if (parcelable instanceof Notification) {

                Notification not = (Notification) parcelable;
                if (not.priority >= Notification.PRIORITY_HIGH) { //should be heads up

                    Bundle extras = not.extras;
                    String title = null, text = null;

                    if (packageName == null || packageName.isEmpty())
                        packageName = "notification";

                    if (extras.containsKey("android.title")) {
                        title = extras.getString("android.title") + "";
                    }

                    if (extras.containsKey("android.text")) {
                        text = extras.getString("android.text") + "";
                    }

                    InformationData informationData = new InformationData(packageName, event.getEventType(),
                            now, null, false);

                    informationData.setNotificationData(informationData.new NotificationData(title, text));
                    queue.put(informationData);
                }
            } else { //toast
                List<CharSequence> textData = event.getText();

                if (packageName == null || packageName.isEmpty())
                    packageName = "toast";

                InformationData informationData = new InformationData(packageName, event.getEventType(),
                        now, null, false);

                informationData.setNotificationData(informationData.new NotificationData(
                        textData.get(0).toString(), null));
            }

        }

        else if(packageName != null){
            queue.put(new InformationData(packageName, event.getEventType(), now,
                    getRootInActiveWindow(), false));
        }

        try {
            event.recycle();
        } catch (IllegalStateException e){
            //shouldn't happen anymore, but just to play save ...
        }
    }

    @Override
    public void onInterrupt() {
        //interrupts feedback from this service
        Log.d(TAG, "got interrupted");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(screenOnReceiver);

        if(isScreenOn)
            queue.put(new InformationData(null, 0, System.currentTimeMillis(), null, true));

        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onScreenOn() {
        isScreenOn = true;
    }

    @Override
    public void onScreenOff() {

        if(isScreenOn)
            queue.put(new InformationData(null, 0, System.currentTimeMillis(), null, true));

        isScreenOn = false;
    }

    private boolean isScreenCurrentlyOn(){
        DisplayManager dm = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : dm.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                return true;
            }
        }
        return false;
    }
}
