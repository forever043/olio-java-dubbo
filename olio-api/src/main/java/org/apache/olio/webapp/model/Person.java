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

import java.util.Collection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/*
@NamedQueries(
{
    @NamedQuery(name = "getUserByName",
    query = "SELECT u FROM Person u WHERE u.userName = :userName"),
    @NamedQuery(name = "getPostedEvents",
    query = "SELECT s FROM SocialEvent s WHERE s.submitterUserName = :submitter"),
    @NamedQuery(name = "getIncomingInvitations",
    query = "SELECT i FROM Invitation i WHERE i.candidate.userName = :candidate"),
    @NamedQuery(name = "getOutgoingInvitations",
    query = "SELECT i FROM Invitation i WHERE i.requestor.userName = :requestor")
}
)
*/
        
public class Person implements java.io.Serializable {

    // basic info
    protected int userID;
    protected String userName;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected String summary;
    protected String telephone;
    protected String email;
    protected String imageURL;
    protected String imageThumbURL;
    protected String timezone;
    protected int addressID;

    // ext info
    private Address address;
    private Collection<Person> friends;
    private Collection<Invitation> incomingInvitations;
    private Collection<Invitation> outgoingInvitations;
    private int extFlag = 0;
    public static final int PERSON_EXT_ADDRESS = 1;
    public static final int PERSON_EXT_FRIENDS = 2;
    public static final int PERSON_EXT_INVITATIONS_INCOMING = 4;
    public static final int PERSON_EXT_INVITATIONS_OUTGOING = 8;
    public static final int PERSON_EXT_ALL = 15;

    public Person() { }
    public Person(String userName, String password, String firstName, String lastName, String summary, String email,
            String telephone, String imageURL, String imageThumbURL, String timezone, Address address){
        
        this.userName = userName;
        this.password=password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.summary = summary;
        this.email = email;
        this.telephone = telephone;
        this.imageURL = imageURL;
        this.imageThumbURL = imageThumbURL;
        this.timezone = timezone;
        this.address = address;
        this.extFlag = (address != null) ? PERSON_EXT_ADDRESS : 0;
    }
    public Person(Person p) {
        // basic info
        this.userName = p.userName;
        this.password = p.password;
        this.firstName = p.firstName;
        this.lastName = p.lastName;
        this.summary = p.summary;
        this.email = p.email;
        this.telephone = p.telephone;
        this.imageURL = p.imageURL;
        this.imageThumbURL = p.imageThumbURL;
        this.timezone = p.timezone;
        this.addressID = p.addressID;

        // ext info
        this.extFlag = p.extFlag;
        this.address = p.address;
        this.friends = p.friends;
        this.incomingInvitations = p.incomingInvitations;
        this.outgoingInvitations = p.outgoingInvitations;
    }
    
    // basic info
    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }
    public String getPassword() {
        return password;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    
    public String getSummary(){
        return summary;
    }
    public String getEmail(){
        return email;
    }
    public String getTelephone(){
        return telephone;
    }
    public String getImageURL() {
        return imageURL;
    }
    public String getImageThumbURL() {
        return imageThumbURL;
    }
    public String getTimezone() {
        return timezone;
    }
    public int getAddressID() {
        return addressID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public void setTelephone(String telephone){
        this.telephone=telephone;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public void setImageThumbURL(String imageThumbURL) {
        this.imageThumbURL = imageThumbURL;
    }
    public void setTimezone(String timezone) {
        this.timezone=timezone;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    // ext info
    public Address getAddress() {
        if ((extFlag & PERSON_EXT_ADDRESS) == 0)
            throw new IllegalStateException("Person.address not fetched: userName \"" + this.userName + "\"");
        return address;
    }
    public Collection<Person> getFriends() {
        if ((extFlag & PERSON_EXT_FRIENDS) == 0)
            throw new IllegalStateException("Person.friends not fetched: userName \"" + this.userName + "\" extFlag=" + extFlag);
        return friends;
    }
    public Collection<Invitation> getIncomingInvitations() {
        if ((extFlag & PERSON_EXT_INVITATIONS_INCOMING) == 0)
            throw new IllegalStateException("Person.incomingInvitations not fetched: userName \"" + this.userName + "\"");
        return incomingInvitations;
    }
    public int getFriendshipRequests() {
        if ((extFlag & PERSON_EXT_INVITATIONS_INCOMING) == 0)
            throw new IllegalStateException("Person.incomingInvitations not fetched: userName \"" + this.userName + "\"");
        return incomingInvitations.size();
    }
    public Collection<Invitation> getOutgoingInvitations() {
        if ((extFlag & PERSON_EXT_INVITATIONS_OUTGOING) == 0)
            throw new IllegalStateException("Person.outgoingInvitations not fetched: userName \"" + this.userName + "\"");
        return outgoingInvitations;
    }

    public void setAddress(Address address) {
        this.address = address;
        extFlag |= PERSON_EXT_ADDRESS;
    }
    public void setFriends(Collection<Person> friends) {
        this.friends = friends;
        extFlag |= PERSON_EXT_FRIENDS;
    }
    public void setIncomingInvitations(Collection<Invitation> incomingInvitations) {
        this.incomingInvitations = incomingInvitations;
        extFlag |= PERSON_EXT_INVITATIONS_INCOMING;
    }
    public void setOutgoingInvitations(Collection<Invitation> outgoingInvitations) {
        this.outgoingInvitations = outgoingInvitations;
        extFlag |= PERSON_EXT_INVITATIONS_OUTGOING;
    }

    /**
     * This method checks to make sure the class values are valid
     *
     * @return Message(s) of validation errors or and empty array (zero length) if class is valid
     */
/*
    public String[] validateWithMessage() {
        ArrayList<String> valMess=new ArrayList<String>();
        if(firstName == null || firstName.equals("")) {
            valMess.add(WebappUtil.getMessage("invalid_first_name"));
        }
        return valMess.toArray(new String[valMess.size()]);
    }
*/

    public boolean equals(Object object) {
        if (!(object instanceof Person)) {
            return false;
        }
        Person other = (Person)object;
        return this.userName.equals(other.getUserName());
    }
 
    // UI only
    private boolean hasReceivedInvitation = false;
    private Collection<Person> nonFriendList = new ArrayList<Person>();
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
    // --- end UI only

}

