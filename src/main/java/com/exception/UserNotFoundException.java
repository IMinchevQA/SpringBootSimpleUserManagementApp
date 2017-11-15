package com.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by Ivan Minchev on 11/13/2017.
 */
public class UserNotFoundException extends UsernameNotFoundException {
    private static final String ENTITY_NOT_FOUND_MESSAGE = "No %s with id '%s' found in the database!";

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String entityType, long id) {
        this(String.format(ENTITY_NOT_FOUND_MESSAGE, entityType, String.valueOf(id)));
    }
}
