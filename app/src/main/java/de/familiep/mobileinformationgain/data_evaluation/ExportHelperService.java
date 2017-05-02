package de.familiep.mobileinformationgain.data_evaluation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.TimeZone;

import de.familiep.mobileinformationgain.R;
import de.familiep.mobileinformationgain.data_evaluation.behaviours.CustomEvaluator;
import de.familiep.mobileinformationgain.data_evaluation.behaviours.DeflateCompressionEvaluation;
import de.familiep.mobileinformationgain.data_evaluation.behaviours.EvaluationBehavior;
import de.familiep.mobileinformationgain.io.FileHelper;
import de.familiep.mobileinformationgain.persistence2.DbReadAccessHelper;
import de.familiep.mobileinformationgain.utils.SharedprefsHelper;
import de.familiep.mobileinformationgain.view.MainActivity;

public class ExportHelperService extends Service implements ExportProgressCallback {

    private final int NOTIFICATION_ID = 428;
    private EvaluationBehavior evaluationBehavior;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager notMngr;
    private NotificationCompat.Builder notBuilder;
    private SharedprefsHelper sharedprefsHelper;
    private ExportHelperService exportHelperService;
    private FileHelper fileHelper;

    private boolean deflateCompressionFinished, customEvaluationFinished;
    private boolean exportSuccessful;

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return null; //no interaction with client
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sharedprefsHelper = new SharedprefsHelper(this);
//        evaluationBehavior = new DeflateCompressionEvaluation(this);
        evaluationBehavior = new CustomEvaluator(this);

        fileHelper = new FileHelper(getFilesDir());

        notMngr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notBuilder = new NotificationCompat.Builder(this);

        startForeground(NOTIFICATION_ID, notBuilder.build());
        exportHelperService = this;

        export();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        if(!exportSuccessful) {
            sharedprefsHelper.setFinishedExporting(false);
            sharedprefsHelper.setExporting(false);

            notBuilder.setContentTitle(getString(R.string.not_export_failed_title))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(
                            getString(R.string.not_export_failed_desc)))
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setPriority(Notification.PRIORITY_MAX);
            notMngr.notify(543, notBuilder.build());
        }

        super.onDestroy();
    }

    private void export(){
        //aquire wakelock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                ExportHelperService.class.getName());
        wakeLock.acquire();

        //setup notification
        notBuilder.setContentTitle(getResources().getString(R.string.not_export_title_1))
            .setSmallIcon(R.drawable.ic_cached_black_24dp)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setAutoCancel(true);

        //start actual exporting
        runExport();

    }

    @Override
    public void notifyProgress(long current, long max) {
        int maxInt;
        int currInt;
        if(max > Integer.MAX_VALUE){
            maxInt = (int) max / 1000000000;
            currInt = (int) current / 1000000000;
        }
        else{
            maxInt = (int) max;
            currInt = (int) current;
        }
        notBuilder.setProgress(maxInt, currInt, false);
        notMngr.notify(NOTIFICATION_ID, notBuilder.build());
    }

    @Override
    public void notifyFinished() {

        if(evaluationBehavior instanceof CustomEvaluator){ //started with custom, so now run deflate
            customEvaluationFinished = true;
            evaluationBehavior = new DeflateCompressionEvaluation(this);
            notBuilder.setContentTitle(getResources().getString(R.string.not_export_title_2));
            runExport();

        } else if (evaluationBehavior instanceof DeflateCompressionEvaluation){
            deflateCompressionFinished = true;
        }

        if(customEvaluationFinished && deflateCompressionFinished){
            finalizeExport();
        }
    }

    private void runExport(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DbReadAccessHelper dbAccessHelper = new DbReadAccessHelper(exportHelperService);
                evaluationBehavior.generateAllData(dbAccessHelper, fileHelper, exportHelperService);
            }
        }).start();
    }

    private void finalizeExport(){
        if(wakeLock != null && wakeLock.isHeld()){
            wakeLock.release();
        }

        //log phone data
        createPhoneInfoFile();

        File datazip = fileHelper.exportAllGeneratedDataAsZip();

        SharedprefsHelper sharedprefsHelper = new SharedprefsHelper(this);
        sharedprefsHelper.setFinishedExporting(true);
        sharedprefsHelper.setExportedDataPath(datazip.getAbsolutePath());

        notBuilder.setContentTitle(getResources().getString(R.string.not_export_title_finished));
        notBuilder.setContentText(getResources().getString(R.string.not_export_desc_finished));
        notBuilder.setProgress(0, 0, false);

        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.putExtra(MainActivity.EXTRA_SHOW_SHARE_DIALOG, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notBuilder.setContentIntent(pendingIntent);
        notBuilder.setSmallIcon(R.drawable.ic_check_black_24dp);

        notMngr.notify(NOTIFICATION_ID+1, notBuilder.build());

        exportSuccessful = true;
        stopSelf();
    }

    private void createPhoneInfoFile(){

        JSONObject json = new JSONObject();

        try{
            json.put("uuid", sharedprefsHelper.getUUID());
            json.put("model", Build.MODEL);
            json.put("manufacturer",Build.MANUFACTURER);
            json.put("brand", Build.BRAND);
            json.put("sdk", Build.VERSION.SDK_INT);
            json.put("versioncode", Build.VERSION.RELEASE);

            //to detect custom roms:
            json.put("user", Build.USER);
            json.put("display", Build.DISPLAY);

            json.put("timezone", TimeZone.getDefault().getID());

        } catch (JSONException e){
            e.printStackTrace();
        }

        String phoneInfo = json.toString();
        fileHelper.createFileWithContent("phoneinfo.json", phoneInfo);
    }
}
