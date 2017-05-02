package de.familiep.mobileinformationgain.persistence2;

import android.provider.BaseColumns;

public final class DbContract {

    private DbContract(){}

    public static class EventSeries implements BaseColumns{
        public static final String TABLE_NAME = "eventseries";
        public static final String COL_NAME_BEGINNING_TIMESTAMP = "beginningTimestamp";
        public static final String COL_NAME_ENDING_TIMESTAMP = "endingTimestamp";
    }

    public static class Events implements BaseColumns{
        public static final String TABLE_NAME = "events";
        public static final String COL_NAME_TIMESTAMP = "timestamp";
        public static final String COL_NAME_PACKAGENAME = "packagename";
        public static final String COL_NAME_EVENTSERIES_ID = "eventseries_id"; //foreign
    }

    public static class EventContents implements BaseColumns{
        public static final String TABLE_NAME = "eventcontents";
        public static final String COL_NAME_TEXTCONTENT = "textContent";
        public static final String COL_NAME_DESCCONTENT = "descContent";
        public static final String COL_NAME_EVENT_ID = "event_id"; //foreign
    }
}
