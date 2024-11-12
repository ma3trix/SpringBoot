package com.obsidi.feedapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.obsidi.feedapp.jpa.User;
import com.obsidi.feedapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;
import java.time.Instant;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user")
@CrossOrigin(exposedHeaders = "Authorization")
public class UserController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @GetMapping("/")
    public List<User> listUsers() {
        logger.debug("The listUsers() method was invoked!");
        return this.userService.listUsers();
    }

    @GetMapping("/{username}")
    public Optional<User> findByUsername(@PathVariable String username) {
        logger.debug("The findByUsername() method was invoked! username={}", username);
        return this.userService.findByUsername(username);
    }

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

    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        logger.debug("Signing up, username: {}", user.getUsername());
        return this.userService.signup(user);
    }

    @GetMapping("/verify/email")
    public ResponseEntity<String> verifyEmail() {
        logger.debug("Verifying Email");

        try {
            this.userService.verifyEmail();
            return ResponseEntity.ok("Email verified successfully");
        } catch (Exception e) {
            logger.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Email verification failed");
        }
    }

    @PostMapping("/reset")
    public void passwordReset(@RequestBody JsonNode json) {

        logger.debug("Resetting Password, password: {}", json.get("password").asText());

        this.userService.resetPassword(json.get("password").asText());
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        logger.debug("Authenticating, username: {}, password: {}", user.getUsername(), user.getPassword());

        // Authenticate the user and generate JWT
        user = this.userService.authenticate(user);
        HttpHeaders jwtHeader = this.userService.generateJwtHeader(user.getUsername());

        logger.debug("User Authenticated, username: {}", user.getUsername());

        return new ResponseEntity<>(user, jwtHeader, OK);
    }
}
