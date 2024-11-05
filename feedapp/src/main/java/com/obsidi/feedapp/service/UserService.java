package com.obsidi.feedapp.service;

import com.obsidi.feedapp.jpa.User;
import com.obsidi.feedapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}