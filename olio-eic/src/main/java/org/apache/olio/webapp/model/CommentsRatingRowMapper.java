package org.apache.olio.webapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CommentsRatingRowMapper implements RowMapper {
    public Object mapRow(ResultSet rs, int index) throws SQLException {
        CommentsRating comment = new CommentsRating();
        Person person = new Person();
        person.setUserName(rs.getString("USERNAME_USERNAME"));

        comment.setCommentsRatingID(rs.getInt("commentsid"));
        comment.setSocialEventID(rs.getInt("SOCIALEVENT_SOCIALEVENTID"));
        comment.setUserName(person);
        comment.setComments(rs.getString("COMMENTS"));
        comment.setRating(rs.getInt("RATING"));
        comment.setCreationTime(rs.getTimestamp("CREATIONTIME"));
        return comment;
    }
}
