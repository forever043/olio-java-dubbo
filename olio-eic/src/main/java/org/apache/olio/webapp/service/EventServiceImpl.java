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
import org.apache.olio.webapp.model.SocialEvent;
import org.apache.olio.webapp.model.SocialEventRowMapper;

@Transactional
public class EventServiceImpl implements EventService {

    private Logger logger = Logger.getLogger(EventServiceImpl.class.getName());
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Collection<SocialEvent> getSocialEvents(String userName) {
        String sql = "select e.* from PERSON_SOCIALEVENT p inner join SOCIALEVENT e on p.SOCIALEVENTID=e.SOCIALEVENTID where p.UserName=?";
        return jdbcTemplate.query(sql, new Object[]{userName}, new SocialEventRowMapper());
    }

    public void attendSocialEvent(int eventID, String userName) {
        String sql = "insert into PERSON_SOCIALEVENT (SOCIALEVENTID, USERNAME) values (?,?)";
        jdbcTemplate.update(sql,
            new Object[]{eventID, userName},
            new int[]{java.sql.Types.INTEGER, java.sql.Types.VARCHAR});
    }
    public void quitSocialEvent(int eventID, String userName) {
        String sql = "delete PERSON_SOCIALEVENT where SOCIALEVENTID=? and USERNAME=?";
        jdbcTemplate.update(sql,
            new Object[]{eventID, userName},
            new int[]{java.sql.Types.INTEGER, java.sql.Types.VARCHAR});
    }

    public List<SocialEvent> getSocialEvents(Map<String, Object> qMap) {
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
            eventsPerPage = (Integer) value;
        if ((value = qMap.get("orderBy")) != null)
            orderBy = (Integer) value;

        StringBuilder qstrb = new StringBuilder();
        qstrb.append("SELECT i ");

        // strb is used to hold the qhere clause so that it can reused for the Count query
        StringBuilder strb = new StringBuilder();
        strb.append("FROM SocialEvent i WHERE ");
        boolean zipSet = false;
        if (zip != null && zip.trim().length() != 0) {
            strb.append("i.address.zip = '" + zip + "' AND ");
            zipSet = true;
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

        List<SocialEvent> socialEvents = jdbcTemplate.query(qstrb.toString(), new SocialEventRowMapper());

        Long size = 0l;
        if (socialEvents != null && socialEvents.size() == eventsPerPage) {
            qstrb = new StringBuilder("SELECT COUNT(i) ");
            qstrb.append(strb);
            size = (Long) jdbcTemplate.queryForLong(qstrb.toString());
        } else if (socialEvents != null) {
            size = (long) socialEvents.size();
        }

        qMap.put("listSize", size);
        return socialEvents;
    }


    public String sayHello(String name) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]" 
                           + "Hello " + name + ", "
                           + "request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response form provider: " + RpcContext.getContext().getLocalAddress();
    }

}
