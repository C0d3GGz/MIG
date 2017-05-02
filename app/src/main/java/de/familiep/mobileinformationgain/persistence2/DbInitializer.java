package de.familiep.mobileinformationgain.persistence2;

import de.familiep.mobileinformationgain.persistence2.DbContract.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbInitializer extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "EventEntries.db";

    public DbInitializer(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_EVENTSERIES_TABLE =
        "CREATE TABLE " + EventSeries.TABLE_NAME + " (" +
        EventSeries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        EventSeries.COL_NAME_BEGINNING_TIMESTAMP + " INTEGER NOT NULL, " +
        EventSeries.COL_NAME_ENDING_TIMESTAMP + " INTEGER);";

    private static final String SQL_CREATE_EVENTS_TABLE =
        "CREATE TABLE " + Events.TABLE_NAME + " (" +
        Events._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        Events.COL_NAME_TIMESTAMP + " INTEGER NOT NULL," +
        Events.COL_NAME_PACKAGENAME + " TEXT NOT NULL," +
        Events.COL_NAME_EVENTSERIES_ID + " INTEGER," +
        " FOREIGN KEY (" + Events.COL_NAME_EVENTSERIES_ID + ") REFERENCES " + EventSeries.TABLE_NAME + "(" + EventSeries._ID + "));";

    private static final String SQL_CREATE_EVENTCONTENTS_TABLE =
        "CREATE TABLE " + EventContents.TABLE_NAME + " (" +
        EventContents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        EventContents.COL_NAME_TEXTCONTENT + " TEXT," +
        EventContents.COL_NAME_DESCCONTENT + " TEXT," +
        EventContents.COL_NAME_EVENT_ID + " INTEGER," +
        " FOREIGN KEY (" + EventContents.COL_NAME_EVENT_ID + ") REFERENCES " + Events.TABLE_NAME + "(" + Events._ID + "));";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EVENTSERIES_TABLE);
        db.execSQL(SQL_CREATE_EVENTS_TABLE);
        db.execSQL(SQL_CREATE_EVENTCONTENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
