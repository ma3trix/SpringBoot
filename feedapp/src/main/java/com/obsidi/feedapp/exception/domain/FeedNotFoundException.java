package com.obsidi.feedapp.exception.domain;

public class FeedNotFoundException extends RuntimeException {

    public FeedNotFoundException(String message) {

        super(message);

    }

}