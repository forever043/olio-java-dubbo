package org.apache.olio.webapp.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.olio.webapp.model.SocialEvent;
import org.apache.olio.webapp.model.SocialEventTag;

public interface EventService {

    public SocialEvent getSocialEvent(int eid);
    public Collection<SocialEvent> getSocialEventsByTag(String tag);
    public Collection<SocialEvent> getSocialEvents(String userName);

    public SocialEvent addSocialEvent(SocialEvent event, String tags);
    public SocialEvent updateSocialEvent(SocialEvent event, String tags);
    public void deleteSocialEvent(int eid);

    public void attendSocialEvent(int eventID, String userName);
    public void quitSocialEvent(int eventID, String userName);

    public SocialEvent updateSocialEventRating(String userName, int eid, int rating);
    public SocialEvent updateSocialEventComment(String userName, int eid, String comments);

    public class SocialEventsResult implements java.io.Serializable {
        public List<SocialEvent> events;
        public long count;
        public SocialEventsResult(List<SocialEvent> events, long count) {
            this.events = events;
            this.count = count;
        }
    };
    SocialEventsResult getSocialEvents(Map<String, Object> qMap);
    public List<SocialEventTag> getSocialEventTags(int topN);

    public SocialEventsResult getUpcomingEvents(String userName, Map<String, Object> qMap);
    public List<SocialEvent>  getPostedEvents(String userName);

    String sayHello(String name);
}
