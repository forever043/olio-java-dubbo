package org.apache.olio.webapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class InvitationRowMapper implements RowMapper {
    public Object mapRow(ResultSet rs, int index) throws SQLException {
        Invitation inv = new Invitation();
        Person req = new Person();
        req.setUserName(rs.getString("REQUESTOR_USERNAME"));
        req.setFirstName(rs.getString("REQUESTOR_FIRSTNAME"));
        req.setLastName(rs.getString("REQUESTOR_LASTNAME"));
        Person cand = new Person();
        cand.setUserName(rs.getString("CANDIDATE_USERNAME"));
        cand.setFirstName(rs.getString("CANDIDATE_FIRSTNAME"));
        cand.setLastName(rs.getString("CANDIDATE_LASTNAME"));

        inv.setInvitationID(rs.getInt("INVITATIONID"));
        inv.setIsAccepted(rs.getBoolean("ISACCEPTED"));
        inv.setRequestor(req);
        inv.setCandidate(cand);
        return inv;
    }
}
