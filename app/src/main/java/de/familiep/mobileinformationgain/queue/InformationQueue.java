package de.familiep.mobileinformationgain.queue;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

import de.familiep.mobileinformationgain.persistence2.InformationDataToDb;

public class InformationQueue {

    private final String TAG = InformationQueue.class.getName();

    private LinkedBlockingQueue<InformationData> queue;
    private InformationDataToDb persister;
    private PowerManager.WakeLock wakeLock;

    public InformationQueue(PowerManager.WakeLock wakeLock, final Context context) {

        this.wakeLock = wakeLock;
        queue = new LinkedBlockingQueue<>();

        new Thread() {
            @Override
            public void run() {
                persister = new InformationDataToDb(context);
                work();
            }
        }.start();
    }

    public void put(InformationData data){

        try {
            queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void work(){
        InformationData data;
        try {
            while ((data = queue.take()) != null){

                if(!wakeLock.isHeld()){
                    wakeLock.acquire();
                }

                Log.d(TAG, "working on queue data, size: " + queue.size());

                persister.addInformationData(data);

                if(queue.size() == 0){
                    wakeLock.release();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
