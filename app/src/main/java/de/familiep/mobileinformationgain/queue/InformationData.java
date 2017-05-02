package de.familiep.mobileinformationgain.queue;

import android.view.accessibility.AccessibilityNodeInfo;

public class InformationData {

    private String packageName;
    private int eventType;
    private long timestamp;
    private AccessibilityNodeInfo rootInfo;
    private boolean isLastOfSeries;
    private NotificationData notificationData;

    public InformationData(String packageName, int eventType, long timestamp,
                           AccessibilityNodeInfo rootInfo, boolean isLastOfSeries) {

        this.packageName = packageName;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.rootInfo = rootInfo;
        this.isLastOfSeries = isLastOfSeries;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public AccessibilityNodeInfo getRootInfo() {
        return rootInfo;
    }

    public void setRootInfo(AccessibilityNodeInfo rootInfo) {
        this.rootInfo = rootInfo;
    }

    public boolean isLastOfSeries() {
        return isLastOfSeries;
    }

    public void setLastOfSeries(boolean lastOfSeries) {
        isLastOfSeries = lastOfSeries;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

    public class NotificationData {
        private String title, description;

        public NotificationData(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
