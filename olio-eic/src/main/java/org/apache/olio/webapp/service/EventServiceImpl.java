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
package org.apache.olio.webapp.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.GregorianCalendar;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.rpc.RpcContext;

import org.apache.olio.webapp.util.WebappConstants;
import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.model.SocialEvent;
import org.apache.olio.webapp.model.SocialEventTag;
import org.apache.olio.webapp.model.CommentsRating;
import org.apache.olio.webapp.model.Address;
import org.apache.olio.webapp.model.PersonRowMapper;
import org.apache.olio.webapp.model.SocialEventRowMapper;
import org.apache.olio.webapp.model.SocialEventTagRowMapper;
import org.apache.olio.webapp.model.CommentsRatingRowMapper;
import org.apache.olio.webapp.model.AddressRowMapper;

@Transactional
public class EventServiceImpl implements EventService {

    private Logger logger = Logger.getLogger(EventServiceImpl.class.getName());
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public SocialEvent getSocialEvent(int eid) {
        SocialEvent event = null;
        String sql = "select * from SOCIALEVENT where SOCIALEVENTID=?";
        event = (SocialEvent)jdbcTemplate.queryForObject(sql, new Object[]{eid}, new SocialEventRowMapper());

        // Fetch ext info
        //   - address
        Address address = (Address)jdbcTemplate.queryForObject(
            "SELECT * FROM ADDRESS a WHERE a.AddressID = ?",
            new Object[]{event.getAddressID()},
            new AddressRowMapper());  
        event.setAddress(address);
        //   - tags
        List<SocialEventTag> tags = (List<SocialEventTag>)jdbcTemplate.query(
            "select t.* from SOCIALEVENTTAG t inner join SOCIALEVENTTAG_SOCIALEVENT i on t.SOCIALEVENTTAGID=i.SOCIALEVENTTAGID where i.SOCIALEVENTID=?",
            new Object[]{event.getSocialEventID()},
            new SocialEventTagRowMapper());  
        event.setTags(tags);
        //   - attendees
        List<Person> attendees = (List<Person>)jdbcTemplate.query(
            "select p.* from PERSON p inner join PERSON_SOCIALEVENT i on p.USERNAME=i.USERNAME where i.SOCIALEVENTID=?",
            new Object[]{event.getSocialEventID()},
            new PersonRowMapper());  
        event.setAttendees(attendees);
        //   - comments
        List<CommentsRating> comments = (List<CommentsRating>)jdbcTemplate.query(
            "select * from COMMENTS_RATING where SOCIALEVENT_SOCIALEVENTID=?",
            new Object[]{event.getSocialEventID()},
            new CommentsRatingRowMapper());  
        event.setComments(comments);


        return event;
    }

    public Collection<SocialEvent> getSocialEvents(String userName) {
        if (userName == null)
            return null;
        String sql = "select e.* from PERSON_SOCIALEVENT p inner join SOCIALEVENT e on p.SOCIALEVENTID=e.SOCIALEVENTID where p.UserName=?";
        return jdbcTemplate.query(sql, new Object[]{userName}, new SocialEventRowMapper());
    }

    public Collection<SocialEvent> getSocialEventsByTag(String tagName) {
        if (tagName == null)
            return null;
        String sql = "select e.* from SOCIALEVENTTAG_SOCIALEVENT t inner join SOCIALEVENT e on t.SOCIALEVENTID=e.SOCIALEVENTID where t.SOCIALEVENTTAGID = (select SOCIALEVENTTAGID from SOCIALEVENTTAG where TAG=?)";
        return jdbcTemplate.query(sql, new Object[]{tagName}, new SocialEventRowMapper());
    }

    public void attendSocialEvent(int eventID, String userName) {
        String sql = "insert into PERSON_SOCIALEVENT (SOCIALEVENTID, USERNAME) values (?,?)";
        jdbcTemplate.update(sql,
            new Object[]{eventID, userName},
            new int[]{java.sql.Types.INTEGER, java.sql.Types.VARCHAR});
    }
    public void quitSocialEvent(int eventID, String userName) {
        String sql = "delete from PERSON_SOCIALEVENT where SOCIALEVENTID=? and USERNAME=?";
        jdbcTemplate.update(sql,
            new Object[]{eventID, userName},
            new int[]{java.sql.Types.INTEGER, java.sql.Types.VARCHAR});
    }

    public SocialEventsResult getSocialEvents(Map<String, Object> qMap) {
        int day = 0, month = 0, year = 0, startIndex = 0, eventsPerPage = WebappConstants.ITEMS_PER_PAGE;
        int orderBy = WebappConstants.ORDER_BY_ASCENDING;
        String zip = null, orderType = null;

        orderType = (String) qMap.get("orderType");
        zip = (String) qMap.get("zip");

        Object value;
        if ((value = qMap.get("day")) != null)
            day = (Integer) value;
        if ((value = qMap.get("month")) != null)
            month = (Integer) value;
        if ((value = qMap.get("year")) != null)
            year = (Integer) value;
        if ((value = qMap.get("startIndex")) != null)
            startIndex = (Integer) value;
        if ((value = qMap.get("eventsPerPage")) != null)
            eventsPerPage = ((Integer)value>0) ? (Integer) value : eventsPerPage;
        if ((value = qMap.get("orderBy")) != null)
            orderBy = (Integer) value;

        StringBuilder qstrb = new StringBuilder();
        qstrb.append("SELECT i.* ");

        // strb is used to hold the qhere clause so that it can reused for the Count query
        StringBuilder strb = new StringBuilder();
        strb.append("FROM SOCIALEVENT i ");
        boolean zipSet = false;
        if (zip != null && zip.trim().length() != 0) {
            strb.append("INNER JOIN ADDRESS a on i.ADDRESS_ADDRESSID=a.ADDRESSID WHERE ");
            strb.append("a.zip = '" + zip + "' AND ");
            zipSet = true;
        }
        else {
            strb.append("WHERE ");
        }

        boolean dateSet = false;
        Timestamp lts = null;
        Timestamp uts = null;
        if (day > 0 && month > 0 && year > 0) {
            GregorianCalendar cal = new GregorianCalendar(year, month - 1, day, 0, 0);
            lts = new Timestamp(cal.getTimeInMillis());
            cal.add(Calendar.DATE, 1);
            uts = new Timestamp(cal.getTimeInMillis());
            strb.append("i.eventTimestamp >= '" + lts + "' AND i.eventTimestamp < '" + uts + "' ");
            dateSet = true;
        } else {
            strb.append("i.eventTimestamp >= CURRENT_TIMESTAMP ");
        }

        qstrb.append(strb);
        if (orderType != null && orderType.equals(WebappConstants.ORDER_CREATED)) {
            qstrb.append("ORDER BY i.createdTimestamp ");
        } else {
            qstrb.append("ORDER BY i.eventTimestamp ");
        }
        if (orderBy == WebappConstants.ORDER_BY_ASCENDING) {
            qstrb.append("ASC");
        } else {
            qstrb.append("DESC");
        }
        
        String sql = qstrb.toString() + " LIMIT ?,?";
        List<SocialEvent> socialEvents = jdbcTemplate.query(sql,
            new Object[]{startIndex, eventsPerPage},
            new SocialEventRowMapper());

        Long size = 0l;
        if (socialEvents != null && socialEvents.size() == eventsPerPage) {
            qstrb = new StringBuilder("SELECT COUNT(SOCIALEVENTID) ");
            qstrb.append(strb);
            size = (Long) jdbcTemplate.queryForLong(qstrb.toString());
        } else if (socialEvents != null) {
            size = (long) socialEvents.size();
        }

        return new SocialEventsResult(socialEvents, size);
    }

    public List<SocialEventTag> getSocialEventTags(int topN) {
        String sql = "SELECT * FROM SOCIALEVENTTAG ORDER BY refCount DESC limit 0,?";
        return jdbcTemplate.query(sql, new Object[]{topN}, new SocialEventTagRowMapper());
    }

    public SocialEventsResult getUpcomingEvents(String userName, Map<String, Object> qMap) {
        int startIndex = 0, eventsPerPage = WebappConstants.ITEMS_PER_PAGE;
        Integer value = (Integer) qMap.get("startIndex");
        if (value != null) {
            startIndex = value;
        }
        value = (Integer) qMap.get("eventsPerPage");
        if (value != null) {
            eventsPerPage = (value>0) ? value : eventsPerPage;
        }

        String sql = "SELECT e.* FROM SOCIALEVENT e "
                   + "INNER JOIN PERSON_SOCIALEVENT p on e.SOCIALEVENTID=p.SOCIALEVENTID "
                   + "WHERE p.userName= ? AND e.eventTimestamp >= CURRENT_TIMESTAMP "
                   + "ORDER BY e.eventTimestamp ASC "
                   + "LIMIT ?,?";
        List<SocialEvent> events = jdbcTemplate.query(sql,
            new Object[]{userName, startIndex, eventsPerPage},
            new SocialEventRowMapper());

        Long l = 0l;
        // We need a count of the events for paging.
        // However, this is not required if eventsPerPage is less than the ITEMS_PER_PAGE
        if (eventsPerPage >= WebappConstants.ITEMS_PER_PAGE) {
            if (events != null && events.size() == eventsPerPage) {
                // Get the count for these events
                //qstr = "SELECT COUNT(e) FROM SocialEvent e JOIN e.attendees p WHERE p.userName= :uname";
                sql = "SELECT count(*) FROM SOCIALEVENT e "
                    + "INNER JOIN PERSON_SOCIALEVENT p on e.SOCIALEVENTID=p.SOCIALEVENTID "
                    + "WHERE p.userName= ? AND e.eventTimestamp >= CURRENT_TIMESTAMP";
                l = (Long) jdbcTemplate.queryForLong(sql, new Object[]{userName});
            } else if (events != null) {
                l = (long) events.size();
            }
        } else if (events != null) {
            l = (long) events.size();
        }

        return new SocialEventsResult(events, l);
    }

    public List<SocialEvent>  getPostedEvents(String userName) {
        String sql = "SELECT * FROM SOCIALEVENT WHERE submitterUserName = ?";
        return jdbcTemplate.query(sql, new Object[]{userName}, new SocialEventRowMapper());
    }

    public String sayHello(String name) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]" 
                           + "Hello " + name + ", "
                           + "request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response form provider: " + RpcContext.getContext().getLocalAddress();
    }

}
