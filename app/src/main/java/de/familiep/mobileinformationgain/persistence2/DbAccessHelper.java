package de.familiep.mobileinformationgain.persistence2;

import de.familiep.mobileinformationgain.persistence2.model.EventContent;
import de.familiep.mobileinformationgain.persistence2.DbContract.*;
import de.familiep.mobileinformationgain.utils.UriMatcher;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public class DbAccessHelper {

    private SQLiteDatabase db;
    private DbInitializer dbInitializer;

    public DbAccessHelper(DbInitializer dbInitializer) {
        this.dbInitializer = dbInitializer;
    }

    public long insertEventSeries(long beginningTimestamp){
        ContentValues values = new ContentValues();
        values.put(EventSeries.COL_NAME_BEGINNING_TIMESTAMP, beginningTimestamp);
        db = dbInitializer.getWritableDatabase();
        return db.insert(EventSeries.TABLE_NAME, null, values);
    }

    public void finishEventSeries(long endingTimestamp, long eventSeriesId){
        ContentValues values = new ContentValues();
        values.put(EventSeries.COL_NAME_ENDING_TIMESTAMP, endingTimestamp);
        String where = EventSeries._ID + " == " + eventSeriesId;

        reopenDbIfNeeded();
        db.update(EventSeries.TABLE_NAME, values, where, null);
        db.close();
    }

    public long insertEvent(long timestamp, String packageName, long eventSeriesId){
        ContentValues values = new ContentValues();
        values.put(Events.COL_NAME_TIMESTAMP, timestamp);
        values.put(Events.COL_NAME_PACKAGENAME, packageName);
        values.put(Events.COL_NAME_EVENTSERIES_ID, eventSeriesId);

        reopenDbIfNeeded();
        return db.insert(Events.TABLE_NAME, null, values);
    }

    //maybe optimize with faster sql statement
    public void addPackagenameToEvent(String packageName, long eventId){
        String[] select = {Events.COL_NAME_PACKAGENAME};
        String where = Events._ID + " == " + eventId;

        reopenDbIfNeeded();
        Cursor cursor = db.query(Events.TABLE_NAME, select, where, null, null, null, null);

        String currentPackagenames = null;
        if(cursor.moveToNext()){
            currentPackagenames = cursor.getString(cursor.getColumnIndexOrThrow(
                    Events.COL_NAME_PACKAGENAME));
        }
        cursor.close();

        if(currentPackagenames != null){
            String[] packageNameArray = currentPackagenames.split(",");

            for (String aPackageName : packageNameArray) {
                if (aPackageName.equals(packageName))
                    return;
            }

            String newPackageNames = currentPackagenames + "," + packageName;
            ContentValues values = new ContentValues();
            values.put(Events.COL_NAME_PACKAGENAME, newPackageNames);
            db.update(Events.TABLE_NAME, values, where, null);

        }
    }

    public void insertBulkEventContent(List<EventContent> eventContentList, long eventId){
        reopenDbIfNeeded();
        db.beginTransaction();
        try{
            for(EventContent eventContent : eventContentList){
                ContentValues values = new ContentValues();

                String content = eventContent.getContent();
                if(content != null){
                    content = content.trim();
                    content = UriMatcher.getShortenedDomainIfUri(content);
                }

                String desc = eventContent.getDesc();
                if (desc != null) {
                    desc = desc.trim();
                    desc = UriMatcher.getShortenedDomainIfUri(desc);
                }

                values.put(EventContents.COL_NAME_TEXTCONTENT, content);
                values.put(EventContents.COL_NAME_DESCCONTENT, desc);
                values.put(EventContents.COL_NAME_EVENT_ID, eventId);
                db.insert(EventContents.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } catch(Exception e){
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    public void insertEventContent(String content, long currentEventId) {

        content = content.trim();
        content = UriMatcher.getShortenedDomainIfUri(content);

        ContentValues values = new ContentValues();
        values.put(EventContents.COL_NAME_TEXTCONTENT, content);
        values.put(EventContents.COL_NAME_EVENT_ID, currentEventId);

        reopenDbIfNeeded();
        db.insert(EventContents.TABLE_NAME, null, values);
    }

    private void reopenDbIfNeeded(){
        if(!db.isOpen()){
            db = dbInitializer.getWritableDatabase();
        }
    }
}
