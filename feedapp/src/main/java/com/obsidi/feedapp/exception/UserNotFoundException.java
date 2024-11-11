package com.obsidi.feedapp.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {

        super(message);

    }

}