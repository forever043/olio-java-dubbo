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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
//import javax.transaction.UserTransaction;
/*
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import static javax.ejb.TransactionAttributeType.REQUIRED;
*/

import com.alibaba.dubbo.rpc.RpcContext;
import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.service.uic.PersonService;


public class PersonServiceImpl implements PersonService {

    @PersistenceUnit(unitName = "BPWebappPu")
    private EntityManagerFactory emf;
    //@Resource
    //private UserTransaction utx;

    private Logger logger = Logger.getLogger(PersonServiceImpl.class.getName());

    public String sayHello(String name) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]" 
                           + "Hello " + name + ", "
                           + "request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response form provider: " + RpcContext.getContext().getLocalAddress();
    }

    public Person validLogin(String userName, String password) {
        return null;
    }

    //@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Person getPerson(String userName) {
        EntityManager em = emf.createEntityManager();
        logger.finest("In findPerson for " + userName);
        try {
            Query q = em.createNamedQuery("getUserByName");
            q.setParameter("userName", userName);
            List<Person> users = q.getResultList();
            if (users.size() < 1) {
                logger.warning("Person with username = " + userName + " not found");
                return null;
            } else {
                logger.severe("Person with username = " + userName + " FOUND OK!!");
                Person person = users.get(0);
                person.getAddress();
                person.getIncomingInvitations();
                person.getFriends();
                //person.getSocialEvents();
                return person;
            }
        } finally {
            em.close();
        }
    }

    public void addPerson(Person person) {
    }

    public void updatePerson(Person person) {
    }

    public void deletePerson(String person) {
    }

    public List<Person> searchPerson(String query, int maxResult) {
        return null;
    }
}
