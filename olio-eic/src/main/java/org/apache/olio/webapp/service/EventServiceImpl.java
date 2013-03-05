package org.apache.olio.webapp.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.core.PreparedStatementCreator;
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
        try {
            Address address = (Address)jdbcTemplate.queryForObject(
                "SELECT * FROM ADDRESS a WHERE a.AddressID = ?",
                new Object[]{event.getAddressID()},
                new AddressRowMapper());  
            event.setAddress(address);
        }
        catch (org.springframework.dao.EmptyResultDataAccessException e) {
            event.setAddress(null);
        }

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

    @Transactional(rollbackFor=Exception.class)
    public void deleteSocialEvent(int eid) {
        SocialEvent event = getSocialEvent(eid);
        if (event == null)
            return;

        // remove relations and entity
        jdbcTemplate.update(
            "delete from SOCIALEVENTTAG_SOCIALEVENT where SOCIALEVENTID=?",
            new Object[] { eid }, new int[] { java.sql.Types.INTEGER });
        jdbcTemplate.update(
            "delete from PERSON_SOCIALEVENT where SOCIALEVENTID=?",
            new Object[] { eid }, new int[] { java.sql.Types.INTEGER });
        jdbcTemplate.update(
            "delete from SOCIALEVENT where SOCIALEVENTID=?",
            new Object[] { eid }, new int[] { java.sql.Types.INTEGER });
        // remove address
        if (event.getAddress() != null) {
            jdbcTemplate.update(
                "delete from ADDRESS where ADDRESSID=?",
                new Object[] { event.getAddress().getAddressID() }, new int[] { java.sql.Types.INTEGER });
        }

        // update tag refcount
        List<SocialEventTag> tags = event.getTags();
        if (tags != null) {
            for (SocialEventTag tag : tags) {
                if (tag.getRefCount() > 1) {
                    jdbcTemplate.update(
                        "update SOCIALEVENTTAG set REFCOUNT=REFCOUNT-1 where SOCIALEVENTTAGID=?",
                        new Object[] { tag.getSocialEventTagID() }, new int[] { java.sql.Types.INTEGER });
                }
                else {
                    jdbcTemplate.update(
                        "delete from SOCIALEVENTTAG where SOCIALEVENTTAGID=?",
                        new Object[] { tag.getSocialEventTagID() }, new int[] { java.sql.Types.INTEGER });
                }
            }
        }
    }

    @Transactional(rollbackFor=Exception.class)
    public SocialEvent addSocialEvent(SocialEvent event, String tags) {

        logger.info("addSocialEvent(\"" + event.getTitle() + "\", \"" + tags + "\"");

        // add entity, new generated eventID store in event.socialEventID
        addSocialEventEntity(event);
        // add social event to related tags
        tagSocialEvent(event, tags);

        // return new added event's title
        return event;
    }

    @Transactional(rollbackFor=Exception.class)
    public SocialEvent updateSocialEvent(SocialEvent event, String tags) {
        // update event.address field
        updateSocialEventAddress(event);
        // add social event to related tags
        tagSocialEvent(event, tags);
        // update social event entity
        updateSocialEventEntity(event);
        // return new added event's title
        return event;
    }

    @Transactional(rollbackFor=Exception.class)
    public void updateSocialEventAddress(SocialEvent event) {
        final Address cAddress = event.getAddress();
        if (event.getAddressID() == 0) {
            KeyHolder addressKey = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(
                        "insert into ADDRESS (STATE, COUNTRY, LATITUDE, LONGITUDE, CITY, ZIP, STREET1, STREET2) values (?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS); 
                    ps.setString(1, cAddress.getState());
                    ps.setString(2, cAddress.getCountry());
                    ps.setDouble(3, cAddress.getLatitude());
                    ps.setDouble(4, cAddress.getLongitude());
                    ps.setString(5, cAddress.getCity());
                    ps.setString(6, cAddress.getZip());
                    ps.setString(7, cAddress.getStreet1());
                    ps.setString(8, cAddress.getStreet2());
                    return ps;
                }}, addressKey);
            event.setAddressID(addressKey.getKey().intValue());
        }
        else {
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
                    event.getAddressID() },
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
    }
        
    @Transactional(rollbackFor=Exception.class)
    private void tagSocialEvent(SocialEvent event, String tags) {
        // get tag list from string, use the set to avoid duplicates
        HashSet hTagSet = new HashSet<String>();
        if (tags != null) {
            StringTokenizer stTags = new StringTokenizer(tags, " ");
            while (stTags.hasMoreTokens()) {
                String tagx = stTags.nextToken().trim().toLowerCase();
                if (tagx.length() == 0 || hTagSet.contains(tagx)) {
                    // The tag is a duplicate, ignore
                    logger.finer("Duplicate tag or empty tag -- " + tagx);
                    continue;
                }
                hTagSet.add(tagx);
                logger.info("Pared tag: " + tagx);
            }
        }

        // Create a single query that returns all the existing tags
        Iterator<String> iter;
        List<SocialEventTag> ltags = null;
        int size = hTagSet.size();
        if (size > 0) {
            StringBuilder strb = new StringBuilder("SELECT * FROM SOCIALEVENTTAG t WHERE t.tag IN (");
            iter = hTagSet.iterator();
            int i = 0;
            while (iter.hasNext()) {
                String tag = iter.next();
                strb.append("'" + tag + "'");
                if (++i < size)
                    strb.append(", ");
            }
            strb.append(")");
            logger.info("Tag query SQL: " + strb.toString());
            ltags = jdbcTemplate.query(strb.toString(), new SocialEventTagRowMapper());
        }

        // delete old tags
        jdbcTemplate.update(
            "delete from SOCIALEVENTTAG_SOCIALEVENT where SOCIALEVENTID=?",
            new Object[] { event.getSocialEventID() },
            new int[]    { java.sql.Types.INTEGER });

        // process exist tags
        if (ltags != null) {
            for (SocialEventTag ptag : ltags) {
                // increase tag refcount
                String sql = "update SOCIALEVENTTAG set REFCOUNT=REFCOUNT+1 where SOCIALEVENTTAGID=?";
                jdbcTemplate.update(sql, new Object[]{ptag.getSocialEventTagID()}, new int[]{java.sql.Types.INTEGER});
                // assign event with this tag
                sql = "insert into SOCIALEVENTTAG_SOCIALEVENT (SOCIALEVENTID, SOCIALEVENTTAGID) values (?,?)";
                jdbcTemplate.update(sql,
                    new Object[]{event.getSocialEventID(), ptag.getSocialEventTagID()},
                    new int[]{java.sql.Types.INTEGER, java.sql.Types.INTEGER});
                // remove this tag from todo-list
                hTagSet.remove(ptag.getTag());
            }
        }

        // process non-exist tags
        iter = hTagSet.iterator();
        while (iter.hasNext()) {
            final String tag = iter.next();
            // add this tag to database
            KeyHolder tagKey = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(
                        "insert into SOCIALEVENTTAG (TAG, REFCOUNT) values (?,1)",
                        Statement.RETURN_GENERATED_KEYS); 
                    ps.setString(1, tag);
                    return ps;
                }}, tagKey);
            // assign event with this tag
            jdbcTemplate.update(
                "insert into SOCIALEVENTTAG_SOCIALEVENT (SOCIALEVENTID, SOCIALEVENTTAGID) values (?,?)",
                new Object[]{event.getSocialEventID(), tagKey.getKey().longValue()},
                new int[]{java.sql.Types.INTEGER, java.sql.Types.INTEGER});
        }
    }


    @Transactional(rollbackFor=Exception.class)
    private void addSocialEventEntity(SocialEvent event) {
        final String sql = "insert into SOCIALEVENT " +
                     "(DESCRIPTION, TITLE, SUBMITTERUSERNAME, SUMMARY, TELEPHONE, IMAGETHUMBURL, IMAGEURL, " +
                     "LITERATUREURL, EVENTTIMESTAMP, TOTALSCORE, NUMBEROFVOTES, DISABLED, CREATEDTIMESTAMP) " + 
                     "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final SocialEvent cEvent = event;
        KeyHolder keyHolder = new GeneratedKeyHolder();  

        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection)
                throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS); 
                ps.setString(1, cEvent.getDescription());
                ps.setString(2, cEvent.getTitle());
                ps.setString(3, cEvent.getSubmitterUserName());
                ps.setString(4, cEvent.getSummary());
                ps.setString(5, cEvent.getTelephone());
                ps.setString(6, cEvent.getImageThumbURL());
                ps.setString(7, cEvent.getImageURL());
                ps.setString(8, cEvent.getLiteratureURL());
                ps.setTimestamp(9, cEvent.getEventTimestamp());
                ps.setInt(10, cEvent.getTotalScore());
                ps.setInt(11, cEvent.getNumberOfVotes());
                ps.setInt(12, cEvent.getDisabled());
                ps.setTimestamp(13, cEvent.getCreatedTimestamp());
                return ps;
            }
        }, keyHolder);
        event.setSocialEventID(keyHolder.getKey().intValue());
    }

    @Transactional(rollbackFor=Exception.class)
    public void updateSocialEventEntity(SocialEvent event) {
        final SocialEvent cEvent = event;
        final String sql = "update SOCIALEVENT set " +
                     "DESCRIPTION=?, TITLE=?, SUBMITTERUSERNAME=?, SUMMARY=?, TELEPHONE=?, IMAGETHUMBURL=?, IMAGEURL=?, " + 
                     "LITERATUREURL=?, EVENTTIMESTAMP=?, TOTALSCORE=?, NUMBEROFVOTES=?, DISABLED=?, CREATEDTIMESTAMP=?, ADDRESS_ADDRESSID=? " +
                     "where SOCIALEVENTID=?";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection)
                throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, cEvent.getDescription());
                ps.setString(2, cEvent.getTitle());
                ps.setString(3, cEvent.getSubmitterUserName());
                ps.setString(4, cEvent.getSummary());
                ps.setString(5, cEvent.getTelephone());
                ps.setString(6, cEvent.getImageThumbURL());
                ps.setString(7, cEvent.getImageURL());
                ps.setString(8, cEvent.getLiteratureURL());
                ps.setTimestamp(9, cEvent.getEventTimestamp());
                ps.setInt(10, cEvent.getTotalScore());
                ps.setInt(11, cEvent.getNumberOfVotes());
                ps.setInt(12, cEvent.getDisabled());
                ps.setTimestamp(13, cEvent.getCreatedTimestamp());
                ps.setInt(14, cEvent.getAddressID());
                ps.setInt(15, cEvent.getSocialEventID());
                return ps;
            }
        });
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

    @Transactional(rollbackFor=Exception.class)
    public SocialEvent updateSocialEventRating(String userName, int eid, int rating) {
        return null; 
    }

    @Transactional(rollbackFor=Exception.class)
    public SocialEvent updateSocialEventComment(String userName, int eid, String comments) {
        return null; 
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
