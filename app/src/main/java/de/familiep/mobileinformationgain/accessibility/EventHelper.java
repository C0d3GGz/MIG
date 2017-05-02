package de.familiep.mobileinformationgain.accessibility;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import de.familiep.mobileinformationgain.persistence2.model.EventContent;

public class EventHelper {

    private List<EventContent> eventContents;

    public EventHelper() {
        eventContents = new ArrayList<>();
    }

    public List<EventContent> extractEventContent(AccessibilityNodeInfo node) {
        eventContents = new ArrayList<>();
        traverseViewHierarchy(node);
        return eventContents;
    }

    private void traverseViewHierarchy(AccessibilityNodeInfo parent) {

        if (parent == null) return;
        EventContent eventContent = new EventContent();

        if (parent.isVisibleToUser()) {
            if (parent.getText() != null) {
                eventContent.setContent(parent.getText().toString());
            }
            if (parent.getContentDescription() != null) {
                eventContent.setDesc(parent.getContentDescription().toString());
            }
            if (parent.getViewIdResourceName() != null) {
                eventContent.setViewId(parent.getViewIdResourceName());
            }

            eventContents.add(eventContent);

            if (parent.getChildCount() > 0) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    traverseViewHierarchy(parent.getChild(i));
                }
            }

            parent.recycle();
        }
    }

    public String combineEventContentToOneString(List<String> textContent, List<String> descContent,
                                                 String delimiter, boolean useDescButPreferText){

        if(textContent.size() != descContent.size())
            throw new IllegalArgumentException("the lists must be the same size as the " +
                    "index should represent data for one entry.");

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < textContent.size(); i++){
            String text = textContent.get(i);
            String desc = descContent.get(i);

            if(text == null || text.isEmpty()){
                if(useDescButPreferText){
                    if(desc != null && !desc.isEmpty()){
                        stringBuilder.append(desc);
                        stringBuilder.append(delimiter);
                    }
                }
            } else {
                stringBuilder.append(text);
                stringBuilder.append(delimiter);
            }
        }
        return stringBuilder.toString();
    }

    /*
    public String eventToString(Event event, boolean useDescription, String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (EventContent content : event.getEventContents()) {

            if (content.getContent() == null || content.getContent().isEmpty()) { //no text content, consider using desc
                if (useDescription) {
                    if (content.getDesc() != null && !content.getDesc().isEmpty()) {
                        stringBuilder.append(content.getDesc());
                        stringBuilder.append(delimiter);
                    }
                }
            } else {
                stringBuilder.append(content.getContent());
                stringBuilder.append(delimiter);
            }
        }

        return stringBuilder.toString();
    }
    */

}
