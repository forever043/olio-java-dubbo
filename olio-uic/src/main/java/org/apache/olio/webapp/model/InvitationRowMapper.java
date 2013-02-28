package org.apache.olio.webapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class InvitationRowMapper implements RowMapper {
    public Object mapRow(ResultSet rs, int index) throws SQLException {
        Invitation inv = new Invitation();
        Person req = new Person();
        Person cand = new Person();
        req.setUserName(rs.getString("REQUESTOR_USERNAME"));
        cand.setUserName(rs.getString("CANDIDATE_USERNAME"));
        inv.setInvitationID(rs.getInt("INVITATIONID"));
        inv.setIsAccepted(rs.getBoolean("ISACCEPTED"));
        inv.setRequestor(req);
        inv.setCandidate(cand);
        return inv;
    }
}
