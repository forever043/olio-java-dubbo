package org.apache.olio.webapp.service;

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
import org.apache.olio.webapp.model.Address;
import org.apache.olio.webapp.model.Invitation;
import org.apache.olio.webapp.model.PersonRowMapper;
import org.apache.olio.webapp.model.AddressRowMapper;

@Transactional
public class PersonServiceImpl implements PersonService {

    private Logger logger = Logger.getLogger(PersonServiceImpl.class.getName());
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Person validLogin(String userName, String password) {
	Person person = findPerson(userName);
        if (person.getPassword().equals(password)) {
            logger.info("user \"" + userName + "\" login OK");
            return person;
        }
        else {
            logger.info("user \"" + userName + "\" login Failed, should be \"" + person.getPassword() + "\"");
            return null;
        }
    }

    public Person findPerson(String userName) {
        Person person = (Person)jdbcTemplate.queryForObject(
            "SELECT * FROM PERSON u WHERE u.userName = ?",
            new Object[]{userName},
            new PersonRowMapper());  
        return person;
    }

    public Person getPerson(String userName) {
        return getPerson(userName, Person.PERSON_EXT_ALL);
    }

    public Person getPerson(String userName, int fetchFlag) {
        Person person;

        // Fetch basic info
        person = findPerson(userName);
        if (person == null)
            return null;

        // Fetch ext info
        if ((fetchFlag & Person.PERSON_EXT_ALL) != 0) {
            Address address = (Address)jdbcTemplate.queryForObject(
                "SELECT * FROM ADDRESS a WHERE a.AddressID = ?",
                new Object[]{person.getAddressID()},
                new AddressRowMapper());  
            person.setAddress(address);
        }
        if ((fetchFlag & Person.PERSON_EXT_FRIENDS) != 0)
            person.setFriends(getFriends(person.getUserID()));
        if ((fetchFlag & Person.PERSON_EXT_INVITATIONS_INCOMING) != 0)
            person.setIncomingInvitations(this.getIncomingInvitations(userName));
        if ((fetchFlag & Person.PERSON_EXT_INVITATIONS_OUTGOING) != 0)
            person.setOutgoingInvitations(this.getOutgoingInvitations(userName));

        return person;
    }


    public String addPerson(Person person) {
        String sql = "insert into PERSON (userName, password, firstName, lastName, summary, telephone, email, imageURL, imageThumbURL, timezone) " +
                     "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
        String sql = "update PERSON set userName=?, password=?, firstName=?, lastName=?, summary=?, telephone=?, email=?, imageURL=?, imageThumbURL=?, timezone=? " +
                     "where userID=?";
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
                person.getTimezone(),
                person.getUserID()},
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
                java.sql.Types.VARCHAR,
                java.sql.Types.INTEGER});
        // TODO: Update Address
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

    public List<Person> getFriends(int userID) {
        String sql = "select PERSON.* from PERSON_PERSON inner join PERSON on friends_USERID=USERID where Person_USERID=?";
        return jdbcTemplate.query(sql, new Object[]{userID}, new PersonRowMapper());
    }
        
    public Person addFriend(int userID, int friendUserID) {
        return null;
    }

    public List<Invitation> getOutgoingInvitations(String userName) {
        return null;
    }
    public List<Invitation> getIncomingInvitations(String userName) {
        return null;
    }

    public String sayHello(String name) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]" 
                           + "Hello " + name + ", "
                           + "request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response form provider: " + RpcContext.getContext().getLocalAddress();
    }

}
