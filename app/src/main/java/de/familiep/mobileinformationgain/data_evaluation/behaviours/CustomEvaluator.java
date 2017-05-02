package de.familiep.mobileinformationgain.data_evaluation.behaviours;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.StringTokenizer;

import de.familiep.mobileinformationgain.BuildConfig;
import de.familiep.mobileinformationgain.compression.CompressionHelper;
import de.familiep.mobileinformationgain.data_evaluation.ExportProgressCallback;
import de.familiep.mobileinformationgain.io.FileHelper;
import de.familiep.mobileinformationgain.persistence2.DbReadAccessHelper;
import de.familiep.mobileinformationgain.persistence2.model.EventContentEntry;
import de.familiep.mobileinformationgain.persistence2.model.LastEventContentEncounter;
import de.familiep.mobileinformationgain.utils.SharedprefsHelper;

public class CustomEvaluator implements EvaluationBehavior {

    private final String TAG = CustomEvaluator.class.getName();
    private Context context;
    public CustomEvaluator(Context context) {
        this.context = context;
    }

    @Override
    public void generateAllData(DbReadAccessHelper dbAccessHelper, FileHelper fileHelper,
                                ExportProgressCallback callback) {

        SharedprefsHelper sharedprefsHelper = new SharedprefsHelper(context);
        if(sharedprefsHelper.isCustomEvalFinished()){
            callback.notifyFinished();
            return;
        }

        FileHelper.CsvHelper csvHelper;
        String fileName = sharedprefsHelper.getCustomEvalCsvFileName();
        boolean restoreExportProcess = false;

        if(fileName != null && !fileName.isEmpty()){
            //file existing
            csvHelper = fileHelper.getCsvHelper(fileName, null);
            restoreExportProcess = true;
        }
        else {
            fileName = "custom_" + System.currentTimeMillis();

            String csvHeader = "index;timestamp;packagename;beginningTimestamp;endingTimestamp;bytes;compressed;" +
                    "timeSincelastSeen;timeSincelastSeenWithinPackagename;wordcount";

            if(BuildConfig.DEBUG){
                csvHeader += ";content";
            }

            csvHelper = fileHelper.getCsvHelper(fileName, csvHeader);

            sharedprefsHelper.setCustomEvalCsvFileName(fileName);
        }

        long eventContentsCount = dbAccessHelper.getEventContentsCount();
        for(long i = 1L; i < eventContentsCount+1; i++){

            if(restoreExportProcess){
                i = sharedprefsHelper.getCustomEvalUnprocessedEntry();
            }

            if(i % 200 == 0) {
                callback.notifyProgress(i, eventContentsCount);
                Log.d(TAG, "working on: " + i + "/" + eventContentsCount);
            }

            EventContentEntry eventContent = dbAccessHelper.getFullEventContentData(i);

            String content = evaluateContent(eventContent);
            if(content == null) continue;

            LastEventContentEncounter additionalData =
                    dbAccessHelper.getLastSightingInfo(i, content, eventContent.packagename);

            long bytelength = content.getBytes().length;
            long compressedLength = CompressionHelper.compress(content);
            long timeSincelastSeen = eventContent.timestamp - additionalData.lastEncounter;
            long timeSincelastSeenWPackage = eventContent.timestamp - additionalData.lastEncounterWithinPackagename;
            int wordcount = new StringTokenizer(content).countTokens();

            String csvContents = i + ";" + eventContent.timestamp + ";" + eventContent.packagename + ";" +
                    eventContent.screenOnSessionStart + ";" + eventContent.screenOnSessionEnd + ";" +
                    bytelength + ";" + compressedLength + ";" + timeSincelastSeen + ";" +
                    timeSincelastSeenWPackage + ";" + wordcount;

            if(BuildConfig.DEBUG)
                csvContents += ";" + content;

            csvHelper.addEntriesToCsv(csvContents);
            sharedprefsHelper.setCustomEvalUnprocessedEntry(i+1);
        }

        File removeMe = csvHelper.finishCsv(); //TODO: no return statement
        dbAccessHelper.close();
        sharedprefsHelper.setCustomEvalFinished(true);
        callback.notifyFinished();
    }

    private String evaluateContent(EventContentEntry entry){
        if(entry.textContent != null && !entry.textContent.isEmpty())
            return entry.textContent;

        else if(entry.descContent != null && !entry.descContent.isEmpty())
            return entry.descContent;

        else
            return null;
    }
}
