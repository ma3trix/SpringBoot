package com.obsidi.feedapp.service;

import com.obsidi.feedapp.exception.EmailExistException;
import com.obsidi.feedapp.exception.UsernameExistException;
import com.obsidi.feedapp.jpa.User;
import com.obsidi.feedapp.repository.UserRepository;
import com.obsidi.feedapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    // Retrieves all users from the database
    public List<User> listUsers() {
        return this.userRepository.findAll();
    }

    // Finds a user by their username
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    // Saves a new user to the database
    public void createUser(User user) {
        this.userRepository.save(user);
    }

    // Method to validate username and email for duplicates
    private void validateUsernameAndEmail(String username, String emailId) {
        this.userRepository.findByUsername(username).ifPresent(u -> {
            throw new UsernameExistException(String.format("Username already exists, %s", u.getUsername()));
        });

        this.userRepository.findByEmailId(emailId).ifPresent(u -> {
            throw new EmailExistException(String.format("Email already exists, %s", u.getEmailId()));
        });
    }

    // Registers a new user with duplicate checks and password encryption
    public User signup(User user) {
        // Convert username and emailId to lowercase
        user.setUsername(user.getUsername().toLowerCase());
        user.setEmailId(user.getEmailId().toLowerCase());

        // Validate username and email for uniqueness
        this.validateUsernameAndEmail(user.getUsername(), user.getEmailId());

        // Set additional user properties
        user.setEmailVerified(false);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setCreatedOn(Timestamp.from(Instant.now()));

        // Save the user to the database
        this.userRepository.save(user);

        // Send verification email
        this.emailService.sendVerificationEmail(user);

        return user;
    }
}
