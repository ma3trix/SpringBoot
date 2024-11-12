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

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        logger.debug("The createUser() method was invoked!");
        this.userService.createUser(user);
        return ResponseEntity.ok("User Created Successfully");
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
    public ResponseEntity<String> passwordReset(@RequestBody JsonNode json) {
        logger.debug("Resetting Password");

        this.userService.resetPassword(json.get("password").asText());
        return ResponseEntity.ok("Password reset successful");
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        logger.debug("Authenticating, username: {}", user.getUsername());

        // Authenticate the user and generate JWT
        user = this.userService.authenticate(user);
        HttpHeaders jwtHeader = this.userService.generateJwtHeader(user.getUsername());

        logger.debug("User Authenticated, username: {}", user.getUsername());

        return new ResponseEntity<>(user, jwtHeader, OK);
    }

    @GetMapping("/get")
    public User getUser() {
        logger.debug("Getting User Data");
        return this.userService.getUser();
    }

    @GetMapping("/reset/{emailId}")
    public void sendResetPasswordEmail(@PathVariable String emailId) {
        logger.debug("Sending Reset Password Email, emailId: {}", emailId);
        this.userService.sendResetPasswordEmail(emailId);
    }
}
