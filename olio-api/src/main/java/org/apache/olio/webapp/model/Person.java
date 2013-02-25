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

//import java.util.ArrayList;
import java.text.SimpleDateFormat;

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

    private int userID;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String summary;
    private String telephone;
    private String email;
    private String imageURL;
    private String imageThumbURL;
    private String timezone;

    public Person() { }
    public Person(String userName, String password, String firstName, String lastName, String summary, String email,
            String telephone, String imageURL, String imageThumbURL, String timezone){
        
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
    }
    
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
}

