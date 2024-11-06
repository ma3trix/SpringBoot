package com.obsidi.feedapp.service;

import com.obsidi.feedapp.jpa.User;
import com.obsidi.feedapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

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

    // Registers a new user with duplicate email check
    public User signup(User user) {
        // Check for duplicate email
        if (userRepository.findByEmailId(user.getEmailId()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Convert username and emailId to lowercase
        user.setUsername(user.getUsername().toLowerCase());
        user.setEmailId(user.getEmailId().toLowerCase());

        // Set emailVerified to false
        user.setEmailVerified(false);

        // Set createdOn to the current timestamp
        user.setCreatedOn(Timestamp.from(Instant.now()));

        // Save the user to the database
        return this.userRepository.save(user);
    }
}