package de.familiep.mobileinformationgain.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

public class AlarmManagerHelper {

    private AlarmManager alarmManager;

    public AlarmManagerHelper(Context con) {
        alarmManager = (AlarmManager) con.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(long millis, PendingIntent action){
        alarmManager.set(AlarmManager.RTC_WAKEUP, millis, action);
    }
}
