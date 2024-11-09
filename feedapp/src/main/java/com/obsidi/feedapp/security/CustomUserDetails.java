package com.obsidi.feedapp.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.obsidi.feedapp.jpa.User;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Returning null as we don't have specific authorities defined
        return null;
    }

    @Override
    public String getPassword() {
        // Return the user's password
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Return the user's username
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Assuming the account is never expired
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Assuming the account is not locked
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Assuming the credentials are never expired
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Assuming the account is enabled
        return true;
    }
}