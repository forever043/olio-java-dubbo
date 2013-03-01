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

/*
 *
 * Created on August 27, 2007, 3:04 PM
 *
 */

package org.apache.olio.webapp.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CommentsRating implements java.io.Serializable {

    private int socialEventID;
    private int commentsRatingID;
    private Person userName;
    private String comments;
    private int rating;
    private Timestamp creationTime;
    
    public CommentsRating() {
    }

    public CommentsRating (int socialEventID, Person userName, String comments, int rating) {
        this.socialEventID = socialEventID;
        this.userName = userName;
        this.comments = comments;
        this.rating = rating;
        this.creationTime = new Timestamp(System.currentTimeMillis());
    }

    public int getCommentsRatingID() {
        return this.commentsRatingID;
    }
    public void setCommentsRatingID(int id) {
        this.commentsRatingID = id;
    }

    public int hashCode() {
        return commentsRatingID;
    }
    public boolean equals(Object object) {
        if (!(object instanceof CommentsRating)) {
            return false;
        }
        CommentsRating other = (CommentsRating)object;
        return commentsRatingID == other.getCommentsRatingID();
    }

    public String toString() {
        return "org.apache.olio.webapp.model.CommentRating[id=" + commentsRatingID + "]";
    }

    public String getComments() {
        return comments;
    }
    public void setComments (String c) {
        comments = c;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getSocialEventID() {
        return socialEventID;
    }
    public void setSocialEventID(int socialEventID) {
        this.socialEventID = socialEventID;
    }

    public Person getUserName() {
        return userName;
    }

    public void setUserName(Person userName) {
        this.userName = userName;
    }
    
    public String getCommentString() {
        if (comments != null)
            return new String (comments);
        return null;
    }
    
    public void updateComments (String str) {
        comments = str;
    }
    
    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    // majiuyue: TODO - delete me
    public List<String> getRatingGraphic() {
        List<String> list = new ArrayList<String>(5);

        for (int i=0; i<rating; i++) {
            list.add("images/star_on.png\" alt=\"16-star-hot\"");
        }
        for (int i=rating; i<5; i++) {
            list.add("images/star_off.png\" alt=\"16-star-cold\"");
        }
        return list;
    }
}
