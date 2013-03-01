package org.apache.olio.webapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AddressRowMapper implements RowMapper {
    public Object mapRow(ResultSet rs, int index) throws SQLException {
        Address address = new Address();
        address.setAddressID(rs.getInt("AddressID"));
        address.setStreet1(rs.getString("Street1"));
        address.setStreet2(rs.getString("Street2"));
        address.setCity(rs.getString("City"));
        address.setState(rs.getString("State"));
        address.setZip(rs.getString("Zip"));
        address.setCountry(rs.getString("Country"));
        address.setLatitude(rs.getDouble("Latitude"));
        address.setLongitude(rs.getDouble("Longitude"));
        return address;
    }
}
