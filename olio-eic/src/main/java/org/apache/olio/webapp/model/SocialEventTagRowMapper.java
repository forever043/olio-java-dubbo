package org.apache.olio.webapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SocialEventTagRowMapper implements RowMapper {
    public Object mapRow(ResultSet rs, int index) throws SQLException {
        SocialEventTag tag = new SocialEventTag();
        tag.setSocialEventTagID(rs.getInt("SOCIALEVENTTAGID"));
        tag.setTag(rs.getString("TAG"));
        tag.setRefCount(rs.getInt("REFCOUNT"));
        return tag;
    }
}
