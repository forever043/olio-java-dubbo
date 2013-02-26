/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olio.webapp.service.uic;

import java.util.List;
import org.apache.olio.webapp.model.Person;

public interface PersonService {

    Person validLogin(String userName, String password);
    String addPerson(Person person);
    void updatePerson(Person person);
    void deletePerson(String person);
    List<Person> searchPerson(String query, int maxResult);
    
    Person findPerson(String userName);   // Fetch basic info only
    Person getPerson(String userName);	  // Fetch all related informations

    List<String> getFriendsUsername(String userName);
    Person addFriend(String userName, String friendUserName);

    String sayHello(String msg);

}
