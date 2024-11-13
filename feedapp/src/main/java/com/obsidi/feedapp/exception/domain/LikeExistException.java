package com.obsidi.feedapp.exception.domain;

public class LikeExistException extends RuntimeException {

    public LikeExistException(String message) {
        super(message);
    }
}