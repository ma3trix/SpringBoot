package com.obsidi.feedapp.jdbc;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;

public class UserMapper implements RowMapper<UserBean> {
    @Override
    public UserBean mapRow(ResultSet rs, int i) throws java.sql.SQLException {

        /* Create a UserBean object */
        UserBean user = new UserBean();

        /* Populates the UserBean object with data from the resultSet */
        user.setUserId(rs.getInt("userId"));
        user.setFirstName(rs.getString("firstName"));
        user.setLastName(rs.getString("lastName"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setEmailId(rs.getString("emailId"));
        user.setEmailVerified(rs.getBoolean("emailVerified"));
        user.setCreatedOn(rs.getTimestamp("createdOn"));

        /* Return the populated UserBean object */
        return user;
    }
}
