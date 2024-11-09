package com.obsidi.feedapp.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

import com.obsidi.feedapp.repository.UserRepository;
import com.obsidi.feedapp.jpa.User;

@Service("UserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opt = this.userRepository.findByUsername(username);

        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }

        return new CustomUserDetails(opt.get());
    }
}