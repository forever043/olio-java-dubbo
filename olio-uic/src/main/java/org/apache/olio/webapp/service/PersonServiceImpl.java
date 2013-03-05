package org.apache.olio.webapp.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.dubbo.rpc.RpcContext;
import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.model.Address;
import org.apache.olio.webapp.model.Invitation;
import org.apache.olio.webapp.model.PersonRowMapper;
import org.apache.olio.webapp.model.AddressRowMapper;
import org.apache.olio.webapp.model.InvitationRowMapper;

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

    public Person findPerson(String userName) {
        String sql = "SELECT * FROM PERSON u WHERE u.userName = ?";
        Person person = (Person)jdbcTemplate.queryForObject(
            sql,
            new Object[]{userName},
            new PersonRowMapper());  
        return person;
    }
    public Person findPerson(int userID) {
        String sql = "SELECT * FROM PERSON u WHERE u.UserID = ?";
        Person person = (Person)jdbcTemplate.queryForObject(
            sql,
            new Object[]{userID},
            new PersonRowMapper());  
        return person;
    }

    public Person getPerson(String userName) {
        return getPerson(userName, Person.PERSON_EXT_ALL);
    }

    public Person getPerson(String userName, int fetchFlag) {
        Person person;

        logger.info("getPerson(\"" + userName + "\", " + fetchFlag + ")");

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
        if ((fetchFlag & Person.PERSON_EXT_FRIENDS) != 0) {
            person.setFriends(getFriends(person.getUserID()));
            logger.info("friends count: " + person.getFriends().size());
        }
        if ((fetchFlag & Person.PERSON_EXT_INVITATIONS_INCOMING) != 0)
            person.setIncomingInvitations(this.getIncomingInvitations(userName));
        if ((fetchFlag & Person.PERSON_EXT_INVITATIONS_OUTGOING) != 0)
            person.setOutgoingInvitations(this.getOutgoingInvitations(userName));

        return person;
    }

    @Transactional(rollbackFor=Exception.class)
    public Person addPerson(Person person) {
        // Add address first
        KeyHolder addressKey = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection)
                throws SQLException {
                return connection.prepareStatement(
                    "insert into ADDRESS (ZIP) values ('')",
                    Statement.RETURN_GENERATED_KEYS); 
            }}, addressKey);
        person.setAddressID(addressKey.getKey().intValue());
        
        // add person
        jdbcTemplate.update(
            "insert into PERSON (userName, ADDRESS_ADDRESSID) values (?, ?)",
            new Object[] {
                person.getUserName(),
                person.getAddressID()},
            new int[] {
                java.sql.Types.VARCHAR,
                java.sql.Types.INTEGER});

        return getPerson(person.getUserName());
    }

    public void updatePerson(Person person) {
        jdbcTemplate.update(                                                      
            "update PERSON set userName=?, password=?, firstName=?, lastName=?, summary=?, telephone=?, email=?, imageURL=?, imageThumbURL=?, timezone=? where userID=?",
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
        Address cAddress = person.getAddress();
        jdbcTemplate.update(
            "update ADDRESS set STATE=?, COUNTRY=?, LATITUDE=?, LONGITUDE=?, CITY=?, ZIP=?, STREET1=?, STREET2=? where ADDRESSID=?",
            new Object[] {
                cAddress.getState(),
                cAddress.getCountry(),
                cAddress.getLatitude(),
                cAddress.getLongitude(),
                cAddress.getCity(),
                cAddress.getZip(),
                cAddress.getStreet1(),
                cAddress.getStreet2(),
                person.getAddressID() },
            new int[] {
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.DOUBLE,
                java.sql.Types.DOUBLE,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.INTEGER});
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

    public List<Person> searchUsers(String query, int maxResult) {
        String sql = "SELECT * FROM PERSON i WHERE i.userName LIKE " +
                     "\'" + query + "%\'" + " ORDER BY i.userName DESC";
        if (maxResult > 0)
            sql += " limit 0," + maxResult;
        return jdbcTemplate.query(sql, new PersonRowMapper());
    }

    public List<Person> getFriends(int userID) {
        String sql = "select PERSON.* from PERSON_PERSON inner join PERSON on friends_USERID=USERID where Person_USERID=?";
        return jdbcTemplate.query(sql, new Object[]{userID}, new PersonRowMapper());
    }
        
    public Person addFriend(int userID, int friendUserID) {
        String sql = "insert into PERSON_PERSON (Person_USERID, friends_USERID) values (?,?)";
        jdbcTemplate.update(sql, new Object[] { userID, friendUserID }, new int[] { java.sql.Types.INTEGER, java.sql.Types.INTEGER });
        return findPerson(friendUserID);
    }

    public Person addFriend(String userName, String friendUserName) {
        Person user = findPerson(userName);
        Person friend = findPerson(friendUserName);
        if ((user == null) || (friend == null))
            return null;
        return addFriend(user.getUserID(), friend.getUserID());
    }

    public Invitation findInvitation(String requestorUserName, String candidateUserName) {
        Invitation inv = (Invitation)jdbcTemplate.queryForObject(
            "SELECT i.*, r.FIRSTNAME REQUESTOR_FIRSTNAME, r.LASTNAME REQUESTOR_LASTNAME, c.FIRSTNAME CANDIDATE_FIRSTNAME, c.LASTNAME CANDIDATE_LASTNAME " +
            "FROM INVITATION i " +
            "INNER JOIN PERSON r ON i.REQUESTOR_USERNAME=r.USERNAME " +
            "INNER JOIN PERSON c ON i.CANDIDATE_USERNAME=c.USERNAME " +
            "WHERE REQUESTOR_USERNAME=? AND CANDIDATE_USERNAME=?",
            new Object[] { requestorUserName, candidateUserName },
            new InvitationRowMapper());
        return inv;
    }
    public Invitation addInvitation(String requestorUserName, String candidateUserName) {
        Person requestor = findPerson(requestorUserName);
        Person candidate = findPerson(candidateUserName);
        if ((requestor != null) && (candidate != null)) {
            jdbcTemplate.update(
                "insert into INVITATION (ISACCEPTED, REQUESTOR_USERNAME, CANDIDATE_USERNAME) values (0, ?, ?)",
                new Object[]{requestorUserName, candidateUserName},
                new int[]{java.sql.Types.VARCHAR, java.sql.Types.VARCHAR});
        }
        return findInvitation(requestorUserName, candidateUserName);
    }

    public Invitation deleteInvitation(String requestorUserName, String candidateUserName) {
        Invitation inv = findInvitation(requestorUserName, candidateUserName);
        if (inv != null) {
            jdbcTemplate.update(
                "delete from INVITATION where REQUESTOR_USERNAME=? and CANDIDATE_USERNAME=?",
                new Object[] { requestorUserName, candidateUserName },
                new int[] { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR });
        }
        return inv;
    }

    public List<Invitation> getOutgoingInvitations(String userName) {
        String sql = "SELECT i.*, r.FIRSTNAME REQUESTOR_FIRSTNAME, r.LASTNAME REQUESTOR_LASTNAME, c.FIRSTNAME CANDIDATE_FIRSTNAME, c.LASTNAME CANDIDATE_LASTNAME FROM INVITATION i INNER JOIN PERSON r ON i.REQUESTOR_USERNAME=r.USERNAME INNER JOIN PERSON c ON i.CANDIDATE_USERNAME=c.USERNAME WHERE REQUESTOR_USERNAME=?";
        return jdbcTemplate.query(sql, new Object[]{userName}, new InvitationRowMapper());
    }
    public List<Invitation> getIncomingInvitations(String userName) {
        String sql = "SELECT i.*, r.FIRSTNAME REQUESTOR_FIRSTNAME, r.LASTNAME REQUESTOR_LASTNAME, c.FIRSTNAME CANDIDATE_FIRSTNAME, c.LASTNAME CANDIDATE_LASTNAME FROM INVITATION i INNER JOIN PERSON r ON i.REQUESTOR_USERNAME=r.USERNAME INNER JOIN PERSON c ON i.CANDIDATE_USERNAME=c.USERNAME WHERE CANDIDATE_USERNAME=?";
        return jdbcTemplate.query(sql, new Object[]{userName}, new InvitationRowMapper());
    }

    public String sayHello(String name) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]" 
                           + "Hello " + name + ", "
                           + "request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response form provider: " + RpcContext.getContext().getLocalAddress();
    }

}
