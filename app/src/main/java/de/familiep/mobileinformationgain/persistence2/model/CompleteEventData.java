package de.familiep.mobileinformationgain.persistence2.model;

import java.util.List;

public class CompleteEventData {

    private long timestamp, screenOnSessionStared, screenOnSessionEnded;
    private String packageName;
    private List<String> eventTextContents;
    private List<String> eventDescContents;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getScreenOnSessionStarted() {
        return screenOnSessionStared;
    }

    public void setScreenOnSessionStarted(long screenOnSessionStared) {
        this.screenOnSessionStared = screenOnSessionStared;
    }

    public long getScreenOnSessionEnded() {
        return screenOnSessionEnded;
    }

    public void setScreenOnSessionEnded(long screenOnSessionEnded) {
        this.screenOnSessionEnded = screenOnSessionEnded;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getScreenOnSessionStared() {
        return screenOnSessionStared;
    }

    public List<String> getEventTextContents() {
        return eventTextContents;
    }

    public void setEventTextContents(List<String> eventTextContents) {
        this.eventTextContents = eventTextContents;
    }

    public List<String> getEventDescContents() {
        return eventDescContents;
    }

    public void setEventDescContents(List<String> eventDescContents) {
        this.eventDescContents = eventDescContents;
    }
}
