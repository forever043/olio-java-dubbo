package org.apache.olio.webapp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SocialEventTag implements java.io.Serializable, Comparable {
    
    private int socialEventTagID;
    private List<SocialEvent> socialEvents= new ArrayList<SocialEvent>();
    private String tag;
    private int refCount=1;
    
    public SocialEventTag() {
    }
    
    public SocialEventTag(String tag) {
        this.tag=tag;
    }
   
    public int getSocialEventTagID() {
        return socialEventTagID;
    }
    public void setSocialEventTagID(int socialEventTagID) {
        this.socialEventTagID=socialEventTagID;
    }    
    
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag=tag;
    }
    
    public int getRefCount() {
        return refCount;
    }
    public void setRefCount(int refCount) {
        this.refCount=refCount;
    }
    public synchronized void incrementRefCount() {
        refCount++;
    }
    
    public synchronized void decrementRefCount() {
        refCount--;
    }
    
    public List<SocialEvent> getSocialEvents() {
        return socialEvents;
    }
    
    public void setSocialEvents(List<SocialEvent> socialEvents) {
        this.socialEvents=socialEvents;
    }
    
    public boolean socialEventExists(SocialEvent socialEvent) {
        return this.getSocialEvents().contains(socialEvent);
    }

    public int compareTo(Object o) {
        if (o == null || !(o instanceof SocialEventTag))
            return 0;
        
        SocialEventTag t = (SocialEventTag)o;
        return this.tag.compareTo(t.getTag());
    }
}



