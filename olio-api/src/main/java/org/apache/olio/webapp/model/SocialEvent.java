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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SocialEvent implements java.io.Serializable {
    
    // basic info
    private int socialEventID;
    private String title;
    private String submitterUserName;
    private Timestamp createdTimestamp;
    private Timestamp eventTimestamp;
    private String summary;
    private String description;
    private String imageURL;
    private String imageThumbURL;
    private String literatureURL;
    private String telephone;
    private int totalScore;
    private int numberOfVotes;
    private int disabled;
    private int addressID;

    // ext info
    private Address address;
    private List<SocialEventTag> tags = new ArrayList<SocialEventTag>();
    private Collection<Person> attendees = new ArrayList<Person>();
    private Collection<CommentsRating> comments = new ArrayList<CommentsRating>();
    
    public SocialEvent() { }
    public SocialEvent(String title, String summary, String description, String submitterUserName, Address address, String telephone, int totalScore, int numberOfVotes,
            String imageURL, String imageThumbURL, String literatureURL, Timestamp eventTimestamp) {
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.submitterUserName = submitterUserName;
        this.address = address;
        this.totalScore = totalScore;
        this.numberOfVotes = numberOfVotes;
        this.imageURL = imageURL;
        this.imageThumbURL = imageThumbURL;
        this.literatureURL = literatureURL;
        this.telephone = telephone;
        this.eventTimestamp = eventTimestamp;
        this.disabled = 0;
        this.createdTimestamp=new Timestamp(new Date().getTime());
    }


    public int getSocialEventID() {
        return socialEventID;
    }
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    public String getSubmitterUserName() {
        return submitterUserName;
    }
    
    public Address getAddress() {
        return address;
    }
    
    public int getTotalScore(){
        return totalScore;
    }
    public int getNumberOfVotes() {
        return numberOfVotes;
    }
    public int getDisabled() {
        return disabled;
    }
    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }
    
    public Timestamp getEventTimestamp() {
        return eventTimestamp;
    }
    public String getImageURL() {
        return imageURL;
    }
    public String getImageThumbURL() {
        return imageThumbURL;
    }
    public String getLiteratureURL() {
        return literatureURL;
    }
    public String getTelephone() {
        return telephone;
    }
    public int getAddressID() {
        return addressID;
    }
    
    public Collection<Person> getAttendees() {
        return attendees;
    }
    
    public void setAttendees(Collection<Person> attendees) {
        this.attendees=attendees;
    }
    
    public void setSocialEventID(int socialEventID) {
        this.socialEventID = socialEventID;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setSubmitterUserName(String submitterUserName) {
        this.submitterUserName = submitterUserName;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }
    public void setDisabled(int disabled) {
        this.disabled = disabled;
    }
    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp=createdTimestamp;
    }
    public void setEventTimestamp(Timestamp eventTimestamp) {
        this.eventTimestamp=eventTimestamp;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public void setImageThumbURL(String imageThumbURL) {
        this.imageThumbURL = imageThumbURL;
    }
    public void setLiteratureURL(String literatureURL) {
        this.literatureURL = literatureURL;
    }
    
    public void setTelephone(String telephone) {
        this.telephone=telephone;
    }
    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    
    public List<SocialEventTag> getTags() {
        return tags;
    }
    public void setTags(List<SocialEventTag> tags) {
        this.tags=tags;
    }
    
    
    /*Business Methods
     **/
    public void addRating(int score){
        setTotalScore(getTotalScore() + score);
        setNumberOfVotes(getNumberOfVotes()+ 1);
    }
    public int checkAverageRating(){
        int average;
        if (getTotalScore() > 0) {
            average = getTotalScore()/getNumberOfVotes();
        } else {
            average = 0;
        }
        return average;
    }
    
    public String tagsAsString() {
        StringBuffer sbTags=new StringBuffer();
        for(SocialEventTag tag : this.getTags()) {
            sbTags.append(tag.getTag());
            sbTags.append(" ");
        }
        return sbTags.toString().trim();
    }
    
    public boolean containsTag(String sxTag) {
        boolean bRet=false;
        for(SocialEventTag tag : getTags()) {
            if(tag.getTag().equals(sxTag)) {
                bRet=true;
                break;
            }
        }
        return bRet;
    }
    
    
    public boolean isAttending(String userName) {
        if (userName == null || attendees == null || attendees.size() == 0)
            return false;
        
        for(Person person : attendees) {
           if(person.getUserName().equals(userName)){
                return true;
            }
        }
        return false;
    }
    
    
    public Collection<CommentsRating> getComments() {
        return comments;
    }
    
    public void setComments(Collection<CommentsRating> comments) {
        this.comments = comments;
    }
    
    public void addComments(CommentsRating cr) {
        this.comments.add(cr);
    }
    
    public int getNumAttendees() {
        if (attendees != null)
            return attendees.size();
        return 0;
    }
    
    public void addTag (SocialEventTag tag) {
        tags.add (tag);
    }
    
    public void addCommentsRating (CommentsRating com) {
        comments.add (com);
    }
    
    public void addAttendee (Person p) {
        attendees.add (p);
    }
    
    public boolean isAttending (Person attendee) {
        if (attendee == null || attendees == null || attendees.isEmpty())
            return false;
        
        for (Person p: attendees) {
            if (p.equals(attendee))
                return true;
        }
        return false;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public boolean equals(Object object) {
        if (!(object instanceof SocialEvent)) {
            return false;
        }
        SocialEvent other = (SocialEvent)object;
        return socialEventID == other.getSocialEventID();
    }
    
    public String getTagsAsString () {
        if (tags == null || tags.size() == 0)
            return "";
        StringBuilder strb = new StringBuilder(tags.get(0).getTag());
        
        for (int i=0; i<tags.size()-1; i++) {
            strb.append(" ");
            strb.append(tags.get(i+1).getTag());
        }
        
        return strb.toString();
    }
}

