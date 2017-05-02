package de.familiep.mobileinformationgain.persistence2;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.familiep.mobileinformationgain.accessibility.EventHelper;
import de.familiep.mobileinformationgain.persistence2.model.EventContent;
import de.familiep.mobileinformationgain.queue.InformationData;

public class InformationDataToDb {

    private DbAccessHelper dbAccessHelper;
    private EventHelper eventHelper;

    private List<EventContent> oldRawEventContents;
    private long currentSeriesId, currentEventId;

    public InformationDataToDb(Context context) {
        dbAccessHelper = new DbAccessHelper(new DbInitializer(context));
        eventHelper = new EventHelper();
    }

    public void addInformationData(InformationData data){

        if(data.getNotificationData() != null){ //single eventContent

            currentEventId = dbAccessHelper.insertEvent(data.getTimestamp(), data.getPackageName(),
                    currentSeriesId);

            String title = data.getNotificationData().getTitle();
            if(title != null && !title.isEmpty())
                dbAccessHelper.insertEventContent(title, currentEventId);

            String desc = data.getNotificationData().getDescription();
            if(desc != null && !desc.isEmpty())
                dbAccessHelper.insertEventContent(desc, currentEventId);

            oldRawEventContents = null;

            return;
        }

        if(data.isLastOfSeries()){
            dbAccessHelper.finishEventSeries(data.getTimestamp(), currentSeriesId);
            oldRawEventContents = null;
            currentSeriesId = 0L;
            currentEventId = 0L;
            return;
        }

        List<EventContent> rawEventContents = eventHelper.extractEventContent(data.getRootInfo());
        if(rawEventContents.size() == 0) return; //filter empty events

        if(oldRawEventContents != null){
            if(rawEventContents.equals(oldRawEventContents)){
                dbAccessHelper.addPackagenameToEvent(data.getPackageName(), currentEventId);
                return;
            }
        }

        //persisting series
        if(currentSeriesId == 0L){
           currentSeriesId = dbAccessHelper.insertEventSeries(data.getTimestamp());
        }

        //persisting event
        currentEventId = dbAccessHelper.insertEvent(data.getTimestamp(), data.getPackageName(),
                currentSeriesId);


        //persisting event content
        List<EventContent> nonZeroEventContents = new ArrayList<>(); //filter zero information events
        for(EventContent eventContent : rawEventContents){
            if(
                ( eventContent.getContent() != null && !eventContent.getContent().isEmpty() ) ||
                ( eventContent.getDesc()    != null && !eventContent.getDesc().isEmpty() ) ){

                nonZeroEventContents.add(eventContent);
            }
        }

        dbAccessHelper.insertBulkEventContent(nonZeroEventContents, currentEventId);

        oldRawEventContents = rawEventContents;
    }
}
