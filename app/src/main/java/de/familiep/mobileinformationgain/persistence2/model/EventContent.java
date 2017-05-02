package de.familiep.mobileinformationgain.persistence2.model;

public class EventContent {

    private String content, desc, viewId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventContent that = (EventContent) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        return viewId != null ? viewId.equals(that.viewId) : that.viewId == null;

    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (viewId != null ? viewId.hashCode() : 0);
        return result;
    }
}
