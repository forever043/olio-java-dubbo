package org.apache.olio.webapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PersonRowMapper implements RowMapper {
    public Object mapRow(ResultSet rs, int index) throws SQLException {
        Person person = new Person();
        person.setUserID(rs.getInt("USERID"));
        person.setUserName(rs.getString("UserName"));
        person.setPassword(rs.getString("Password"));
        person.setFirstName(rs.getString("FirstName"));
        person.setLastName(rs.getString("LastName"));
        person.setSummary(rs.getString("Summary"));
        person.setEmail(rs.getString("Email"));
        person.setTelephone(rs.getString("Telephone"));
        person.setImageURL(rs.getString("ImageURL"));
        person.setImageThumbURL(rs.getString("ImageThumbURL"));
        person.setTimezone(rs.getString("Timezone"));
        return person;
    }
}
