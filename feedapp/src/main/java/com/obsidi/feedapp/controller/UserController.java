package com.obsidi.feedapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.obsidi.feedapp.jpa.User;
import com.obsidi.feedapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import com.obsidi.feedapp.jpa.Profile;

@CrossOrigin(exposedHeaders = "Authorization")
@RestController
@RequestMapping("/user")
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
    public void verifyEmail() {
        logger.debug("Verifying Email");
        this.userService.verifyEmail();
    }

    @PostMapping("/reset")
    public ResponseEntity<String> passwordReset(@RequestBody JsonNode json) {
        logger.debug("Resetting Password");

        this.userService.resetPassword(json.get("password").asText());
        return ResponseEntity.ok("Password reset successful");
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        logger.debug("Authenticating, username: {}, password: {}", user.getUsername(), user.getPassword());

        // Authenticate the user and generate JWT
        user = this.userService.authenticate(user);
        HttpHeaders jwtHeader = this.userService.generateJwtHeader(user.getUsername());

        logger.debug("User Authenticated, username: {}", user.getUsername());
        logger.debug("Jwtheader: {}", jwtHeader.toString());

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

    @PostMapping("/update")
    public User updateUser(@RequestBody User user) {

        logger.debug("Updating User Data");

        return this.userService.updateUser(user);
    }

    @PostMapping("/update/profile")
    public User updateUserProfile(@RequestBody Profile profile) {

        logger.debug("Updating User Profile Data, Profile: {}", profile.toString());

        return this.userService.updateUserProfile(profile);
    }

    @GetMapping("/auth-check")
    public ResponseEntity<String> authCheck() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return ResponseEntity.ok("Authenticated user: " + auth.getName());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
    }
}
