package de.familiep.mobileinformationgain;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.UUID;

import de.familiep.mobileinformationgain.io.FileHelper;
import de.familiep.mobileinformationgain.utils.SharedprefsHelper;
import de.familiep.mobileinformationgain.view.LaunchingActivity;

public class InformationMeasureApplication extends Application {

    @Override
    public void onCreate() {

        if(!BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable e) {
                    handleUncaughtException(e);
                }
            });
        }

        SharedprefsHelper sharedprefsHelper = new SharedprefsHelper(this);
        if(sharedprefsHelper.getUUID().isEmpty())
            sharedprefsHelper.setUUID(UUID.randomUUID().toString().substring(0, 8));

        super.onCreate();
    }

    public void handleUncaughtException (Throwable e) {

        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();

        //save error file
        FileHelper fileHelper = new FileHelper(getExternalFilesDir(null));
        fileHelper.appendErrorlog(stacktrace);


        //show user that the app did crash
        showNotification();

        SharedprefsHelper sharedprefsHelper = new SharedprefsHelper(getApplicationContext());
        sharedprefsHelper.appCrashed(true);

//        System.exit(1);
    }

    private void showNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.icon2)
                        .setContentTitle(getString(R.string.app_crash_not_title))
                        .setContentText(getString(R.string.app_crash_not_desc))
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_ALL);

//        Intent resultIntent = new Intent(this, LaunchingActivity.class);
//        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//
//        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1721, mBuilder.build());
    }
}
