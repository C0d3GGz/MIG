package de.familiep.mobileinformationgain.alarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import de.familiep.mobileinformationgain.R;
import de.familiep.mobileinformationgain.utils.SharedprefsHelper;
import de.familiep.mobileinformationgain.view.MainActivity;

public class NotificationIntent extends IntentService {

    public static final String NOTIFICATION_INTENT = "notificationintent";
    public static final String DATA_COLLECTION_FINISHED = "datacollectionfinished";

    public NotificationIntent() {
        super(NOTIFICATION_INTENT);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent != null && intent.getAction().equals(DATA_COLLECTION_FINISHED)){
            SharedprefsHelper sharedprefsHelper = new SharedprefsHelper(this);
            sharedprefsHelper.setDataCollectionFinished(true);

            //issue notification
            Intent activityIntent = new Intent(this, MainActivity.class);
            NotificationManager notMngr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this);
            String content = getResources().getString(R.string.not_desc_further_steps);
            notBuilder.setSmallIcon(R.drawable.ic_check_black_24dp)
                .setContentTitle(getResources().getString(R.string.not_inquiry_finished))
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(Notification.PRIORITY_MAX);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 92, activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            notBuilder.setContentIntent(pendingIntent);
            notMngr.notify(93, notBuilder.build());
        }
    }
}
