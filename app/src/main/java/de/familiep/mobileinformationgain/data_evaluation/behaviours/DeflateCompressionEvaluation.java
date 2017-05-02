package de.familiep.mobileinformationgain.data_evaluation.behaviours;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import de.familiep.mobileinformationgain.BuildConfig;
import de.familiep.mobileinformationgain.accessibility.EventHelper;
import de.familiep.mobileinformationgain.compression.CompressionHelper;
import de.familiep.mobileinformationgain.data_evaluation.ExportProgressCallback;
import de.familiep.mobileinformationgain.io.FileHelper;
import de.familiep.mobileinformationgain.persistence2.DbReadAccessHelper;
import de.familiep.mobileinformationgain.persistence2.model.CompleteEventData;
import de.familiep.mobileinformationgain.utils.SharedprefsHelper;

public class DeflateCompressionEvaluation implements EvaluationBehavior{

    private EventHelper eventHelper;
    private Context context;

    public DeflateCompressionEvaluation(Context context) {
        eventHelper = new EventHelper();
        this.context = context;
    }

    @Override
    public void generateAllData(DbReadAccessHelper dbReader, FileHelper fileHelper,
                                ExportProgressCallback callback) {

        SharedprefsHelper sharedprefsHelper = new SharedprefsHelper(context);
        if(sharedprefsHelper.isDeflateFinished()){
            callback.notifyFinished();
            return;
        }

        FileHelper.CsvHelper csvHelper;
        String fileName = sharedprefsHelper.getDeflateCsvFileName();

        long firstUnprocessedEntry = Long.MAX_VALUE; // = lastProcessedEntry + 1
        boolean restoreExportProcess = false;

        if(fileName != null && !fileName.isEmpty()){ //existent
            firstUnprocessedEntry = sharedprefsHelper.getDeflateUnprocessedEntry();
            restoreExportProcess = true;
            csvHelper = fileHelper.getCsvHelper(fileName, null);
            Log.d("familiep", "restoring");
        }

        else {
            fileName = "deflate_" + System.currentTimeMillis();
            String csvHeader = "index;timestamp;packagename;informationdelta;beginningTimestamp;endingTimestamp;wordcount";

            if(BuildConfig.DEBUG)
                csvHeader+= ";content";

            csvHelper = fileHelper.getCsvHelper(fileName,csvHeader);

            sharedprefsHelper.setDeflateCsvFileName(fileName);
        }

        try{
            List<CompleteEventData> dataset = dbReader.getAllEventInformationData();

            long oldCompressedBytes = 0L;
            StringBuilder stringBuilder = new StringBuilder();

            long current = 0L;
            long max = dataset.size();
            for(CompleteEventData eventData : dataset){

                Log.d("familiep", "working on: " + current + "/" + max);

                String eventContents = eventHelper.combineEventContentToOneString(
                        eventData.getEventTextContents(),
                        eventData.getEventDescContents(),
                        "\n",
                        true);

                stringBuilder.append(eventContents);

                if(restoreExportProcess) {
                    if (current < firstUnprocessedEntry - 1) { //we worked on all old entries, ignore them
                        current++;
                        continue;
                    } else if (current == firstUnprocessedEntry - 1) { //we reached old state; restore oldCompressedBytes by compressing all the string-data but the current line
                        oldCompressedBytes = CompressionHelper.compress(stringBuilder.toString());
                        restoreExportProcess = true; //done
                        Log.d("familiep", "finished restoring");
                        current++;
                        continue;
                    }
                }

                long newCompressedBytes = CompressionHelper.compress(stringBuilder.toString());
                long delta = newCompressedBytes - oldCompressedBytes;

                String csvContents = current + ";" + eventData.getTimestamp() + ";" + eventData.getPackageName()
                        + ";" + delta + ";" + eventData.getScreenOnSessionStarted() + ";"
                        + eventData.getScreenOnSessionEnded() + "; " + new StringTokenizer(eventContents).countTokens();

                if(BuildConfig.DEBUG)
                    csvContents += ";" + eventContents;

                csvHelper.addEntriesToCsv(csvContents);
                oldCompressedBytes = newCompressedBytes;

                current++;
                callback.notifyProgress(current, max);
                sharedprefsHelper.setDeflateUnprocessedEntry(current);
                //sharedPrefs; setLastEntry to current;
            }

        } catch (Exception e){ //out of memory etc.
            e.printStackTrace();
            csvHelper.addEntriesToCsv("ERROR");
        } finally {
            csvHelper.finishCsv();
            sharedprefsHelper.setDeflateFinished(true);
            dbReader.close();
            callback.notifyFinished();
        }
    }
}
