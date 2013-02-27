package org.apache.olio.webapp.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.olio.webapp.model.SocialEvent;

public interface EventService {

    public Collection<SocialEvent> getSocialEvents(String userName);

    public void attendSocialEvent(int eventID, String userName);
    public void quitSocialEvent(int eventID, String userName);
    public List<SocialEvent> getSocialEvents(Map<String, Object> qMap);

    String sayHello(String name);
}
