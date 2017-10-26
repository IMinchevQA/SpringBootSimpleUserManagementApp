package com.javainuse.service;

import com.javainuse.model.User;
import org.springframework.stereotype.Service;

/**
 * Created by Ivan Minchev on 10/24/2017.
 */

@Service
public interface UserService {
    void save(User user);

    User findByUsername(String username);
}
