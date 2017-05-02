package de.familiep.mobileinformationgain.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedprefsHelper {

    private static final String PREF_FILE = "prefs";
    private static final String CRASH_KEY = "appcrash";
    private static final String COLLECTION_FINISHED_KEY = "dataCollectionFinished";
    private static final String WELCOME_KEY = "welcomecard";
    private static final String IS_EXPORTING_KEY = "exportingcard";
    private static final String IS_EXPORTING_FINISHED_KEY = "exportingfinished";
    private static final String COLLECTION_TIME_FINISHED_KEY = "collectionfinishedinmillis";

    private static final String IS_DEFLATE_FINISHED_KEY = "isdeflatefinished";
    private static final String DEFLATE_CSV_NAME_KEY = "deflatecsvname";
    private static final String DEFLATE_FIRST_UNPROCESSED_ENTRY = "deflatefirstunprocessedentry";

    private static final String IS_CUSTOM_EVAL_FINISHED_KEY ="iscustomevalfinished";
    private static final String CUSTOM_EVAL_CSV_NAME_KEY = "customevalcsvname";
    private static final String CUSTOM_EVAL_FIRST_UNPROCESSED_ENTRY ="customevalfirstunprocessed";

    private static final String UUID_KEY = "uuid";

    private static final String EXPORT_DATA_PATH_KEY = "exportpath";

    private SharedPreferences sharedPrefs;

    public SharedprefsHelper(Context context) {
        this.sharedPrefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    public boolean didAppCrash(){
        return sharedPrefs.getBoolean(CRASH_KEY, false);
    }

    public void appCrashed(boolean didCrash){
        sharedPrefs.edit().putBoolean(CRASH_KEY, didCrash).commit();
    }

    public boolean shouldShowWelcomeCard(){
        return sharedPrefs.getBoolean(WELCOME_KEY, true);
    }

    public void setShouldShowWelcomeCard(boolean shouldShow){
        sharedPrefs.edit().putBoolean(WELCOME_KEY, shouldShow).apply();
    }

    public void setExporting(boolean isExporting) {
        sharedPrefs.edit().putBoolean(IS_EXPORTING_KEY, isExporting).apply();
    }

    public boolean isExporting() {
        return sharedPrefs.getBoolean(IS_EXPORTING_KEY, false);
    }

    public boolean isDataCollectionFinished() {
        return sharedPrefs.getBoolean(COLLECTION_FINISHED_KEY, false);
    }

    public void setDataCollectionFinished(boolean finished){
        sharedPrefs.edit().putBoolean(COLLECTION_FINISHED_KEY, finished).apply();
    }

    public boolean hasFinishedExporting() {
        return sharedPrefs.getBoolean(IS_EXPORTING_FINISHED_KEY, false);
    }

    public void setFinishedExporting(boolean finished){
        SharedPreferences.Editor editor = sharedPrefs.edit();
        if(finished)
            editor.putBoolean(IS_EXPORTING_KEY, false);

        editor.putBoolean(IS_EXPORTING_FINISHED_KEY, finished);
        editor.apply();
    }

    public long getDataCollectionFinishedMillis() {
        return sharedPrefs.getLong(COLLECTION_TIME_FINISHED_KEY, 0L);
    }

    public void setDataCollectionFinishedMillis(long millis){
        sharedPrefs.edit().putLong(COLLECTION_TIME_FINISHED_KEY, millis).commit();
    }

    public boolean isDeflateFinished() {
        return sharedPrefs.getBoolean(IS_DEFLATE_FINISHED_KEY, false);
    }

    public void setDeflateFinished(boolean finished) {
        sharedPrefs.edit().putBoolean(IS_DEFLATE_FINISHED_KEY, finished).apply();
    }

    public String getDeflateCsvFileName() {
        return sharedPrefs.getString(DEFLATE_CSV_NAME_KEY, null);
    }

    public void setDeflateCsvFileName(String fileName) {
        sharedPrefs.edit().putString(DEFLATE_CSV_NAME_KEY, fileName).apply();
    }

    public long getDeflateUnprocessedEntry() {
        return sharedPrefs.getLong(DEFLATE_FIRST_UNPROCESSED_ENTRY, 0L);
    }

    public void setDeflateUnprocessedEntry(long firstUnprocessedEntry) {
        sharedPrefs.edit().putLong(DEFLATE_FIRST_UNPROCESSED_ENTRY, firstUnprocessedEntry).commit();
    }

    public boolean isCustomEvalFinished() {
        return sharedPrefs.getBoolean(IS_CUSTOM_EVAL_FINISHED_KEY, false);
    }


    public void setCustomEvalFinished(boolean finished) {
        sharedPrefs.edit().putBoolean(IS_CUSTOM_EVAL_FINISHED_KEY, finished).apply();
    }

    public String getCustomEvalCsvFileName() {
        return sharedPrefs.getString(CUSTOM_EVAL_CSV_NAME_KEY, null);
    }


    public void setCustomEvalCsvFileName(String fileName) {
        sharedPrefs.edit().putString(CUSTOM_EVAL_CSV_NAME_KEY, fileName).apply();
    }

    public long getCustomEvalUnprocessedEntry() {
        return sharedPrefs.getLong(CUSTOM_EVAL_FIRST_UNPROCESSED_ENTRY, 1L);
    }

    public void setCustomEvalUnprocessedEntry(long firstUncompressedEntry) {
        sharedPrefs.edit().putLong(CUSTOM_EVAL_FIRST_UNPROCESSED_ENTRY, firstUncompressedEntry).commit();
    }

    public void setExportedDataPath(String absolutePath) {
        sharedPrefs.edit().putString(EXPORT_DATA_PATH_KEY, absolutePath).commit();
    }

    public String getExportedDataPath(){
        return sharedPrefs.getString(EXPORT_DATA_PATH_KEY, null);
    }

    public String getUUID(){
        return sharedPrefs.getString(UUID_KEY, "");
    }

    public void setUUID(String uuid){
        sharedPrefs.edit().putString(UUID_KEY, uuid).apply();
    }
}
