package com.obsidi.feedapp.controller;

import com.obsidi.feedapp.jdbc.UserBean;
import com.obsidi.feedapp.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.obsidi.feedapp.jpa.User;

@RestController
@RequestMapping("/user")
public class UserController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    // Endpoint to list all users
    @GetMapping("/")
    public List<User> listUsers() {
        logger.debug("The listUsers() method was invoked!");
        return this.userService.listUsers();
    }

    // Endpoint to find a user by username
    @GetMapping("/{username}")
    public Optional<User> findByUsername(@PathVariable String username) {
        logger.debug("The findByUsername() method was invoked! username={}", username);
        return this.userService.findByUsername(username);
    }

    // Endpoint to create a new user
    @GetMapping("/{first}/{last}/{username}/{password}/{phone}/{emailId}")
    public String createUser(
            @PathVariable String first,
            @PathVariable String last,
            @PathVariable String username,
            @PathVariable String password,
            @PathVariable String phone,
            @PathVariable String emailId) {

        User user = new User();
        user.setFirstName(first);
        user.setLastName(last);
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setEmailId(emailId);
        user.setEmailVerified(false);
        user.setCreatedOn(Timestamp.from(Instant.now()));

        logger.debug("The createUser() method was invoked! user={}", user);
        this.userService.createUser(user);

        return "User Created Successfully";
    }

    // Endpoint to sign up a new user
    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        logger.debug("Signing up, username: {}", user.getUsername());
        return this.userService.signup(user);
    }
}