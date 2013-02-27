 /*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.olio.webapp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.text.SimpleDateFormat;
import org.apache.olio.webapp.util.WebappUtil;
import static org.apache.olio.webapp.controller.WebConstants.*;
import java.util.Set;
import java.util.HashSet;

public class PersonView extends Person {

    //used for UI display purposes
    private boolean hasReceivedInvitation = false;
    private Collection<Person> nonFriendList = new ArrayList<Person>();        
    
    public PersonView() { }
    public PersonView(String userName, String password, String firstName, String lastName, String summary, String email,
                  String telephone, String imageURL, String imageThumbURL, String timezone, Address address){
        super(userName, password, firstName, lastName, summary, email,
              telephone, imageURL, imageThumbURL, timezone, address);
    }
    public PersonView(Person person) {
        super(person.userName, person.password, person.firstName, person.lastName, person.summary, person.email,
              person.telephone, person.imageURL, person.imageThumbURL, person.timezone, person.getAddress());
    }
    
/*
    public int getFriendshipRequests() {
        return getIncomingInvitations().size();
    }
*/
    
    // TODO: Fix me
    public Collection<SocialEvent> getSocialEvents() {
        return null;
    }

    public boolean isHasReceivedInvitation() {
        return hasReceivedInvitation;
    }
       
    public void setHasReceivedInvitation(boolean hasReceivedInvitation) {
        this.hasReceivedInvitation = hasReceivedInvitation;
    }

    public Collection<Person> getNonFriendList() {
        return nonFriendList;
    }

    public void setNonFriendList(Collection<Person> nonFriendList) {
        this.nonFriendList = nonFriendList;
    }
         
/*
    public void setSocialEvents(Collection<SocialEvent> socialEvents) {
        this.socialEvents=socialEvents;
    }
*/
    
    /**
     * @return String  contains JSON representation of person data,
     *                 inlcuding a list of friends' user NAMES but not
     *                 all the other person info of a friend.
     *                 This is NOT all the person data, it leaves off some data
     *                 such as details of friends and posted events and objects
     *                 it has relationships with, else whole object graph too big.
     *                 It is NOT all data, since cleint view only uses some
     *   for example { "person": { "userName": "sean", "firstName": "sean",
     *                "lastName": "brydon", "summary": "%26nbsp%3Bbio",
     *                "friends": ["inder", "mark", "yuta", "greg"]
     *                "postedEvents": [{"socialEventId": "1001", "title": "BluePrints Party", "date": "04-25-2007"},
     *                                 {"socialEventId": "1002", "title": "JavaOne", "date": "05-08-2007"}]
     *                 }}
     */
    public String toJson() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("{\"person\":{ ");
        sb.append("\"userName\":\"");
        sb.append(WebappUtil.encodeJSONString(userName));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"firstName\":\"");
        sb.append(WebappUtil.encodeJSONString(firstName));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"lastName\":\"");
        sb.append(WebappUtil.encodeJSONString(lastName));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"summary\":\"");
        sb.append(WebappUtil.encodeJSONString(summary));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"email\":\"");
        sb.append(WebappUtil.encodeJSONString(email));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"telephone\":\"");
        sb.append(WebappUtil.encodeJSONString(telephone));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"imageURL\":\"");
        sb.append(WebappUtil.encodeJSONString(imageURL));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"imageThumbURL\":\"");
        sb.append(WebappUtil.encodeJSONString(imageThumbURL));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"address\":\"");
        sb.append(WebappUtil.encodeJSONString(getAddress().toString()));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"timezone\":\"");
        sb.append(WebappUtil.encodeJSONString(timezone));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        //REMOVE soon, just support old page
        //get username of all this person's friends
        sb.append("\"friendNames\":[");
        if (getFriends().size() > 0) {
            for (Person p : getFriends()) {
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(p.getUserName()));
                sb.append(DOUBLE_QUOTE);
                sb.append(COMMA);
            }
            sb.deleteCharAt(sb.length()-1);//remove last space
            sb.deleteCharAt(sb.length()-1);//remove last comma
        }
        sb.append("]");
        sb.append(COMMA);
        //end old junk to remove
        
        //get username of all this person's friends
        sb.append("\"friends\":[");
        if (getFriends().size() > 0) {
            for (Person p : getFriends()) {
                sb.append("{ ");
                sb.append("\"name\":");
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(p.getUserName()));
                sb.append(DOUBLE_QUOTE);
                sb.append(COMMA);
                sb.append("\"imageThumbURL\":\"");
                sb.append(WebappUtil.encodeJSONString(p.getImageThumbURL()));
                sb.append(DOUBLE_QUOTE);
                sb.append("}");
                sb.append(COMMA);
            }
            sb.deleteCharAt(sb.length()-1);//remove last space
            sb.deleteCharAt(sb.length()-1);//remove last comma
        }
        sb.append("]");
        
        //get list of all events posted by person, just titles and dates, not all event info
        boolean addedItems=false;
        sb.append(COMMA);
        sb.append( "\"postedEvents\":[");
        if (getSocialEvents().size() > 0) {
            for (SocialEvent event : getSocialEvents()) {
                if(event.getSubmitterUserName().equals(userName)) {
                    addedItems=true;
                    sb.append("{\"socialEventId\":");
                    sb.append(DOUBLE_QUOTE);
                    sb.append(WebappUtil.encodeJSONString(String.valueOf(event.getSocialEventID())));
                    sb.append(DOUBLE_QUOTE);
                    sb.append(COMMA);
                    sb.append("\"title\":");
                    sb.append(DOUBLE_QUOTE);
                    sb.append(WebappUtil.encodeJSONString(event.getTitle()));
                    sb.append(DOUBLE_QUOTE);
                    sb.append(COMMA);
                    
                    sb.append("\"date\":");
                    SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm aa zzz");
                    String formattedDate = formatter.format(event.getEventTimestamp());
                    sb.append(DOUBLE_QUOTE);
                    sb.append(formattedDate);
                    sb.append(DOUBLE_QUOTE);
                    sb.append("}");
                    sb.append(COMMA);
                }
            }
            if(addedItems) {
                sb.deleteCharAt(sb.length()-1);//remove last space
                sb.deleteCharAt(sb.length()-1);//remove last comma
            }
        }
        sb.append("]");
        
        //get list of all events the person is attending, just titles and dates, not all event info
        sb.append(COMMA);
        sb.append( "\"attendEvents\":[");
        if (getSocialEvents().size() > 0) {
            for (SocialEvent event : getSocialEvents()) {
                sb.append("{\"socialEventId\":");
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(String.valueOf(event.getSocialEventID())));
                sb.append(DOUBLE_QUOTE);
                sb.append(COMMA);
                sb.append("\"title\":");
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(event.getTitle()));
                sb.append(DOUBLE_QUOTE);
                sb.append(COMMA);
                
                sb.append("\"date\":");
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm aa zzz");
                String formattedDate = formatter.format(event.getEventTimestamp());
                sb.append(DOUBLE_QUOTE);
                sb.append(formattedDate);
                sb.append(DOUBLE_QUOTE);
                sb.append("}");
                sb.append(COMMA);
            }
            sb.deleteCharAt(sb.length()-1);//remove last space
            sb.deleteCharAt(sb.length()-1);//remove last comma
        }
        sb.append("]");
        
        sb.append("}}");
        return sb.toString();
    }
    
    public String getPersonAsJson() {
        return toJson();
    }
    
    // Copied from toJson. TODO - cleanup
    public String getFriendsAsJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"friendsList\":[");
        if (getFriends().size() > 0) {
            for (Person p : getFriends()) {
                sb.append("{");
                sb.append(DOUBLE_QUOTE);
                sb.append("name");
                sb.append(DOUBLE_QUOTE);
                sb.append(":");
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(p.getUserName()));
                sb.append(DOUBLE_QUOTE);
                sb.append(COMMA);
                sb.append(DOUBLE_QUOTE);
                sb.append("imageURL");
                sb.append(DOUBLE_QUOTE);
                sb.append(":");
                sb.append(DOUBLE_QUOTE);
                if (p.getImageThumbURL() != null)
                    sb.append(WebappUtil.encodeJSONString(p.getImageThumbURL()));
                sb.append(DOUBLE_QUOTE);
                sb.append("}");
                sb.append(COMMA);
            }
            sb.deleteCharAt(sb.length()-1);//remove last space
            sb.deleteCharAt(sb.length()-1);//remove last comma
        }
        sb.append("]");
        sb.append("}");
        
        return sb.toString();
    }
    
    // Copied from toJson. TODO - fix it
    public String getAttendEventsAsJson() {
        return ModelFacade.eventsToJson(getSocialEvents());
    }

    public String getOutgoingInvitationsAsJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"outgoingInvitations\":[");
        if (getOutgoingInvitations().size() > 0) {
           for (Invitation inv : getOutgoingInvitations()){
               sb.append("{");
               sb.append(DOUBLE_QUOTE);
               sb.append("name");
               sb.append(DOUBLE_QUOTE);
               sb.append(":");
               sb.append(DOUBLE_QUOTE);
               sb.append(WebappUtil.encodeJSONString(inv.getCandidate().getUserName()));
               sb.append(DOUBLE_QUOTE);
               sb.append(COMMA);
               sb.append(DOUBLE_QUOTE);
               sb.append("fullname");
               sb.append(DOUBLE_QUOTE);
               sb.append(":");
               sb.append(DOUBLE_QUOTE);
               sb.append(WebappUtil.encodeJSONString(inv.getCandidate().getFirstName() + " " + inv.getCandidate().getLastName()));
               sb.append(DOUBLE_QUOTE);
               sb.append("}");
           }
           sb.deleteCharAt(sb.length()-1);//remove last space
            sb.deleteCharAt(sb.length()-1);//remove last comma
        }
        sb.append("]");
        sb.append("}");

        return sb.toString();
    }
}
