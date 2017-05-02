package de.familiep.mobileinformationgain.persistence2;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.familiep.mobileinformationgain.persistence2.model.CompleteEventData;
import de.familiep.mobileinformationgain.persistence2.DbContract.*;
import de.familiep.mobileinformationgain.persistence2.model.EventContentEntry;
import de.familiep.mobileinformationgain.persistence2.model.LastEventContentEncounter;

// please... do not read this class

public class DbReadAccessHelper {

    private SQLiteDatabase db;
    private DbInitializer dbInitializer;

    public DbReadAccessHelper(Context context) {
        dbInitializer = new DbInitializer(context);
        db = dbInitializer.getReadableDatabase();
    }

    public List<CompleteEventData> getAllEventInformationData(){

        String selectStatement =
            "SELECT " + EventContents.TABLE_NAME + "." + EventContents.COL_NAME_TEXTCONTENT + "," +
                EventContents.TABLE_NAME +         "." + EventContents.COL_NAME_DESCCONTENT + "," +
                Events.TABLE_NAME +                "." + Events.COL_NAME_TIMESTAMP + "," +
                Events.TABLE_NAME +                "." + Events.COL_NAME_PACKAGENAME + "," +
                EventSeries.TABLE_NAME +           "." + EventSeries.COL_NAME_BEGINNING_TIMESTAMP + "," +
                EventSeries.TABLE_NAME +           "." + EventSeries.COL_NAME_ENDING_TIMESTAMP + " " +

            "FROM " + EventContents.TABLE_NAME + " " +

            "JOIN " + Events.TABLE_NAME + " ON " + EventContents.TABLE_NAME + "." + EventContents.COL_NAME_EVENT_ID +  " = " +
                Events.TABLE_NAME + "." + Events._ID + " " +

            "JOIN "  + EventSeries.TABLE_NAME + " ON " + Events.TABLE_NAME + "." + Events.COL_NAME_EVENTSERIES_ID + " = " +
                EventSeries.TABLE_NAME + "." + EventSeries._ID + ";";

        Cursor cursor = db.rawQuery(selectStatement, null);

        List<CompleteEventData> completeData = new ArrayList<>();
        List<String> textContents = new ArrayList<>();
        List<String> descContents = new ArrayList<>();
        CompleteEventData data = new CompleteEventData();
        long oldTimestamp = 0L;
        while(cursor.moveToNext()) {

            long eventTimeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(Events.COL_NAME_TIMESTAMP));

            if(oldTimestamp == 0L){
                oldTimestamp = eventTimeStamp;

                //initialize
                data.setTimestamp(eventTimeStamp);
                data.setPackageName(cursor.getString(cursor.getColumnIndexOrThrow(Events.COL_NAME_PACKAGENAME)));
                data.setScreenOnSessionStarted(cursor.getLong(cursor.getColumnIndexOrThrow(EventSeries.COL_NAME_BEGINNING_TIMESTAMP)));
                data.setScreenOnSessionEnded(cursor.getLong(cursor.getColumnIndexOrThrow(EventSeries.COL_NAME_ENDING_TIMESTAMP)));
            }

            if(eventTimeStamp == oldTimestamp){ //same event, just changing content
                String textContent = cursor.getString(cursor.getColumnIndexOrThrow(EventContents.COL_NAME_TEXTCONTENT));
                String descContent = cursor.getString(cursor.getColumnIndexOrThrow(EventContents.COL_NAME_DESCCONTENT));

                textContents.add(textContent);
                descContents.add(descContent);
            }
            else{ //new event

                //finish previous event
                data.setEventTextContents(textContents);
                data.setEventDescContents(descContents);
                completeData.add(data);

                //reset all values from previous eventcontent
                textContents = new ArrayList<>();
                descContents = new ArrayList<>();
                data = new CompleteEventData();

                //initialize new CompleteEventData instance
                String packagename = cursor.getString(cursor.getColumnIndexOrThrow(Events.COL_NAME_PACKAGENAME));
                long seriesBeginning = cursor.getLong(cursor.getColumnIndexOrThrow(EventSeries.COL_NAME_BEGINNING_TIMESTAMP));
                long seriesEnding = cursor.getLong(cursor.getColumnIndexOrThrow(EventSeries.COL_NAME_ENDING_TIMESTAMP));

                data.setTimestamp(eventTimeStamp);
                data.setPackageName(packagename);
                data.setScreenOnSessionStarted(seriesBeginning);
                data.setScreenOnSessionEnded(seriesEnding);

                //adding content from new event
                String textContent = cursor.getString(cursor.getColumnIndexOrThrow(EventContents.COL_NAME_TEXTCONTENT));
                String descContent = cursor.getString(cursor.getColumnIndexOrThrow(EventContents.COL_NAME_DESCCONTENT));
                textContents.add(textContent);
                descContents.add(descContent);
            }

            oldTimestamp = eventTimeStamp;
        }

        //finish previous event
        data.setEventTextContents(textContents);
        data.setEventDescContents(descContents);
        completeData.add(data);

        cursor.close();

        return completeData;
    }

    public void close(){
        db.close();
        dbInitializer.close();
    }

    public long getEventContentsCount(){
        Cursor cursor = db.rawQuery("SELECT Count(*) FROM " + EventContents.TABLE_NAME, null);
        cursor.moveToFirst();
        long count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public EventContentEntry getFullEventContentData(long eventContentsId) {
        String selectStatement =
            "SELECT " + EventContents.TABLE_NAME + "." + EventContents.COL_NAME_TEXTCONTENT + "," +
                EventContents.TABLE_NAME +         "." + EventContents.COL_NAME_DESCCONTENT + "," +
                Events.TABLE_NAME +                "." + Events.COL_NAME_TIMESTAMP + "," +
                Events.TABLE_NAME +                "." + Events.COL_NAME_PACKAGENAME + "," +
                EventSeries.TABLE_NAME +           "." + EventSeries.COL_NAME_BEGINNING_TIMESTAMP + "," +
                EventSeries.TABLE_NAME +           "." + EventSeries.COL_NAME_ENDING_TIMESTAMP + " " +

            "FROM " + EventContents.TABLE_NAME + " " +

            "JOIN " + Events.TABLE_NAME + " ON " + EventContents.TABLE_NAME + "." + EventContents.COL_NAME_EVENT_ID +  " = " +
                Events.TABLE_NAME + "." + Events._ID + " " +

            "JOIN "  + EventSeries.TABLE_NAME + " ON " + Events.TABLE_NAME + "." + Events.COL_NAME_EVENTSERIES_ID + " = " +
                EventSeries.TABLE_NAME + "." + EventSeries._ID + " " +
            "WHERE " + EventContents.TABLE_NAME + "." + EventContents._ID + " = " + eventContentsId + ";";

        Cursor cursor = db.rawQuery(selectStatement, null);
        cursor.moveToFirst();

        EventContentEntry newEntry = new EventContentEntry();
        newEntry.timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(Events.COL_NAME_TIMESTAMP));
        newEntry.textContent = cursor.getString(cursor.getColumnIndexOrThrow(EventContents.COL_NAME_TEXTCONTENT));
        newEntry.descContent = cursor.getString(cursor.getColumnIndexOrThrow(EventContents.COL_NAME_DESCCONTENT));
        newEntry.packagename = cursor.getString(cursor.getColumnIndexOrThrow(Events.COL_NAME_PACKAGENAME));
        newEntry.screenOnSessionStart = cursor.getLong(cursor.getColumnIndexOrThrow(EventSeries.COL_NAME_BEGINNING_TIMESTAMP));
        newEntry.screenOnSessionEnd = cursor.getLong(cursor.getColumnIndexOrThrow(EventSeries.COL_NAME_ENDING_TIMESTAMP));

        cursor.close();

        return newEntry;
    }

    public LastEventContentEncounter getLastSightingInfo(long sinceId, String content, String packagename) {
        LastEventContentEncounter encounterData = new LastEventContentEncounter();
        content = DatabaseUtils.sqlEscapeString(content);

        String selectStatement =
                "SELECT " + Events.TABLE_NAME + "." + Events.COL_NAME_TIMESTAMP + " " +

                "FROM " + EventContents.TABLE_NAME + " " +

                "JOIN " + Events.TABLE_NAME + " ON " + EventContents.TABLE_NAME + "." + EventContents.COL_NAME_EVENT_ID +  " = " +
                Events.TABLE_NAME + "." + Events._ID + " " +

                "JOIN "  + EventSeries.TABLE_NAME + " ON " + Events.TABLE_NAME + "." + Events.COL_NAME_EVENTSERIES_ID + " = " +
                EventSeries.TABLE_NAME + "." + EventSeries._ID + " " +

                "WHERE " + EventContents.TABLE_NAME + "." + EventContents._ID + " < " + sinceId + " AND (" + EventContents.COL_NAME_DESCCONTENT + " = " + content + " OR " + EventContents.COL_NAME_TEXTCONTENT + " = " + content + ") " +

                "ORDER BY " + EventContents.TABLE_NAME + "." + EventContents._ID + " DESC " +

                "LIMIT 1;";

        Cursor cursor = db.rawQuery(selectStatement, null);
        if(cursor.moveToFirst())
            encounterData.lastEncounter = cursor.getLong(cursor.getColumnIndexOrThrow(Events.COL_NAME_TIMESTAMP));
        cursor.close();

        String selectStatement2 =
                "SELECT " + Events.TABLE_NAME + "." + Events.COL_NAME_TIMESTAMP + " " +

                "FROM " + EventContents.TABLE_NAME + " " +

                "JOIN " + Events.TABLE_NAME + " ON " + EventContents.TABLE_NAME + "." + EventContents.COL_NAME_EVENT_ID +  " = " +
                Events.TABLE_NAME + "." + Events._ID + " " +

                "JOIN "  + EventSeries.TABLE_NAME + " ON " + Events.TABLE_NAME + "." + Events.COL_NAME_EVENTSERIES_ID + " = " +
                EventSeries.TABLE_NAME + "." + EventSeries._ID + " " +

                "WHERE " + EventContents.TABLE_NAME + "." + EventContents._ID + " < " + sinceId + " AND (" + EventContents.COL_NAME_DESCCONTENT + " = " + content + " OR " + EventContents.COL_NAME_TEXTCONTENT + " = " + content + ") " +
                    " AND " + Events.TABLE_NAME + "." + Events.COL_NAME_PACKAGENAME + " LIKE '%" + packagename + "%' " +
                "ORDER BY " + EventContents.TABLE_NAME + "." + EventContents._ID + " DESC " +

                "LIMIT 1;";

        Cursor cursor2 = db.rawQuery(selectStatement2, null);
        if(cursor2.moveToFirst())
            encounterData.lastEncounterWithinPackagename = cursor2.getLong(cursor.getColumnIndexOrThrow(Events.COL_NAME_TIMESTAMP));
        cursor2.close();

        return encounterData;
    }
}
