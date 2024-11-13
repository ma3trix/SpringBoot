package com.obsidi.feedapp.exception.domain;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {

        super(message);

    }

}