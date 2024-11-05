package com.obsidi.feedapp.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<UserBean> listUsers() {
        String sql = "SELECT * FROM \"User\"";

        return this.jdbcTemplate.query(sql, new UserMapper());
    }

    public UserBean findByUsername(String username) {

        String sql = "SELECT * FROM \"User\" WHERE username = ?";

        List<UserBean> users = this.jdbcTemplate.query(sql, new UserMapper(), username);

        /*
         * Returns null if users is empty otherwise, returns the first element in the
         * list
         */
        return users.isEmpty() ? null : users.get(0);
    }

    public void createUser(UserBean user) {

        String sql = "INSERT INTO \"User\" (\"firstName\", \"lastName\", username, phone, \"emailId\", password, \"emailVerified\", \"createdOn\") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        logger.debug("Insert Query: {}", sql);

        /* Executes the Insert Statement */
        this.jdbcTemplate.update(sql, new Object[] { user.getFirstName(), user.getLastName(), user.getUsername(),
                user.getPhone(), user.getEmailId(), user.getPassword(), user.getEmailVerified(), user.getCreatedOn() });

    }
}