package com.service;

import org.springframework.stereotype.Service;

/**
 * Created by Ivan Minchev on 10/24/2017.
 */

@Service
public interface SecurityService {
    String findLoggedInUsername();

    void autologin(String username, String password);
}
