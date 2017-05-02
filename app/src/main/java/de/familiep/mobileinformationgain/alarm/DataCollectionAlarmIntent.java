package de.familiep.mobileinformationgain.alarm;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;

import de.familiep.mobileinformationgain.utils.SharedprefsHelper;

/**
 * use this intent service to create the notification that the data collection has finished.
 * may be used by broadcast receiver after reboot and for the first time setup.
 */
public class DataCollectionAlarmIntent extends IntentService {

    public static final String ALARM_INTENT = "alarmintent";

    public DataCollectionAlarmIntent() {
        super(ALARM_INTENT);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SharedprefsHelper sharedprefsHelper = new SharedprefsHelper(this);
        long millis = sharedprefsHelper.getDataCollectionFinishedMillis();

        if(millis != 0L && !sharedprefsHelper.hasFinishedExporting()) {

            Intent notificationIntent = new Intent(this, NotificationIntent.class);
            notificationIntent.setAction(NotificationIntent.DATA_COLLECTION_FINISHED);
            PendingIntent pendingIntent = PendingIntent.getService(this, 40, notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT);

            AlarmManagerHelper alarmManagerHelper = new AlarmManagerHelper(this);
            alarmManagerHelper.setAlarm(millis, pendingIntent);
        }
    }
}
