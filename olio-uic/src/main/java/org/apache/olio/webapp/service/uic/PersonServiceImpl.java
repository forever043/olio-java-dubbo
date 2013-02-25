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

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.rpc.RpcContext;
import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.model.PersonRowMapper;

@Transactional
public class PersonServiceImpl implements PersonService {

    private Logger logger = Logger.getLogger(PersonServiceImpl.class.getName());
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Person validLogin(String userName, String password) {
	Person person = getPerson(userName);
        if (person.getPassword().equals(password)) {
            logger.info("user \"" + userName + "\" login OK");
            return person;
        }
        else {
            logger.info("user \"" + userName + "\" login Failed, should be \"" + person.getPassword() + "\"");
            return null;
        }
    }

    public Person getPerson(String userName) {
        Person person = (Person)jdbcTemplate.queryForObject(
            "SELECT * FROM PERSON u WHERE u.userName = ?",
            new Object[]{userName},
            new PersonRowMapper());  
        return person;
    }

    public String addPerson(Person person) {
        String sql = "insert into PERSON (userName, password, firstName, lastName, summary, telephone, email, imageURL, imageThumbURL, timezone) values (?)";
        jdbcTemplate.update(
            sql,
            new Object[] {
                person.getUserName(),
                person.getPassword(),
                person.getFirstName(),
                person.getLastName(),
                person.getSummary(),
                person.getTelephone(),
                person.getEmail(),
                person.getImageURL(),
                person.getImageThumbURL(),
                person.getTimezone()},
            new int[] {
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR});
        return person.getUserName();
    }

    public void updatePerson(Person person) {
    }

    @Transactional(rollbackFor=Exception.class)
    public void deletePerson(String userName) {
        Person person = getPerson(userName);
        if (person == null)
            return;
        jdbcTemplate.update(
            "delete from PERSON where userID=?",
            new Object[]{person.getUserID()},
            new int[]{java.sql.Types.INTEGER});
    }

    public List<Person> searchPerson(String query, int maxResult) {
        String sql = "SELECT * FROM PERSON i WHERE i.userName LIKE " +
                     "\'" + query + "%\'" + " ORDER BY i.userName DESC " +
                     "limit 0," + maxResult;
        return jdbcTemplate.query(sql, new PersonRowMapper());
    }

/*
    public String sayHello(String name) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]" 
                           + "Hello " + name + ", "
                           + "request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response form provider: " + RpcContext.getContext().getLocalAddress();
    }
*/

}
