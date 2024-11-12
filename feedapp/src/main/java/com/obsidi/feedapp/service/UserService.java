package com.obsidi.feedapp.service;

import com.obsidi.feedapp.exception.EmailNotVerifiedException;
import com.obsidi.feedapp.exception.EmailExistException;
import com.obsidi.feedapp.exception.UserNotFoundException;
import com.obsidi.feedapp.exception.UsernameExistException;
import com.obsidi.feedapp.jpa.User;
import com.obsidi.feedapp.repository.UserRepository;
import com.obsidi.feedapp.provider.ResourceProvider;
import com.obsidi.feedapp.security.JwtService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    @Autowired
    ResourceProvider provider;

    public List<User> listUsers() {
        return this.userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public void createUser(User user) {
        this.userRepository.save(user);
    }

    private void validateUsernameAndEmail(String username, String emailId) {
        this.userRepository.findByUsername(username).ifPresent(u -> {
            throw new UsernameExistException(String.format("Username already exists, %s", u.getUsername()));
        });

        this.userRepository.findByEmailId(emailId).ifPresent(u -> {
            throw new EmailExistException(String.format("Email already exists, %s", u.getEmailId()));
        });
    }

    public User signup(User user) {
        user.setUsername(user.getUsername().toLowerCase());
        user.setEmailId(user.getEmailId().toLowerCase());
        this.validateUsernameAndEmail(user.getUsername(), user.getEmailId());
        user.setEmailVerified(false);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setCreatedOn(Timestamp.from(Instant.now()));
        this.userRepository.save(user);
        this.emailService.sendVerificationEmail(user);
        return user;
    }

    public void verifyEmail() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
        user.setEmailVerified(true);
        this.userRepository.save(user);
    }

    private static User isEmailVerified(User user) {
        if (user.getEmailVerified().equals(false)) {
            throw new EmailNotVerifiedException(String.format("Email requires verification, %s", user.getEmailId()));
        }
        return user;
    }

    private Authentication authenticate(String username, String password) {
        return this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
    }

    public void sendResetPasswordEmail(String emailId) {

        Optional<User> opt = this.userRepository.findByEmailId(emailId);

        if (opt.isPresent()) {
            this.emailService.sendResetPasswordEmail(opt.get());
        } else {
            logger.debug("Email doesn't exist, {}", emailId);
        }
    }

    public void resetPassword(String password) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

        user.setPassword(this.passwordEncoder.encode(password));

        this.userRepository.save(user);
    }

    public User authenticate(User user) {
        this.authenticate(user.getUsername(), user.getPassword());
        User authenticatedUser = this.userRepository.findByUsername(user.getUsername())
                .map(UserService::isEmailVerified)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Temporary line for testing purposes
        authenticatedUser.setEmailVerified(true);
        this.userRepository.save(authenticatedUser);

        return authenticatedUser;
    }

    public User getUser() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        /* Get User from the DB. */
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
    }

    public HttpHeaders generateJwtHeader(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, this.jwtService.generateJwtToken(username, this.provider.getJwtExpiration()));
        return headers;
    }
}
