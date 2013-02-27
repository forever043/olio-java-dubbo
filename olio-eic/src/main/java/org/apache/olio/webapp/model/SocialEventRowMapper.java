package org.apache.olio.webapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SocialEventRowMapper implements RowMapper {
    public Object mapRow(ResultSet rs, int index) throws SQLException {
        SocialEvent event = new SocialEvent();
        event.setSocialEventID(rs.getInt("SocialEventID"));
        event.setTitle(rs.getString("TITLE"));
        event.setSubmitterUserName(rs.getString("SUBMITTERUSERNAME"));
        event.setCreatedTimestamp(rs.getTimestamp("CREATEDTIMESTAMP"));
        event.setEventTimestamp(rs.getTimestamp("EVENTTIMESTAMP"));
        event.setSummary(rs.getString("SUMMARY"));
        event.setDescription(rs.getString("DESCRIPTION"));
        event.setImageURL(rs.getString("IMAGEURL"));
        event.setImageThumbURL(rs.getString("IMAGETHUMBURL"));
        event.setLiteratureURL(rs.getString("LITERATUREURL"));
        event.setTelephone(rs.getString("TELEPHONE"));
        event.setTotalScore(rs.getInt("TOTALSCORE"));
        event.setNumberOfVotes(rs.getInt("NUMBEROFVOTES"));
        event.setDisabled(rs.getInt("DISABLED"));
        event.setAddressID(rs.getInt("ADDRESS_ADDRESSID"));
        return event;
    }
}
