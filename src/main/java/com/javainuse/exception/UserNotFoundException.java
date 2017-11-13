package com.javainuse.exception;

/**
 * Created by Ivan Minchev on 11/13/2017.
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
