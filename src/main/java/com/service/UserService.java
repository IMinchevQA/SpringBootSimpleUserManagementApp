package com.service;

import com.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by Ivan Minchev on 10/24/2017.
 */

@Service
public interface UserService {

    Page<User> listUsers(Pageable pageable);

    User findByUsername(String username);

    User findById(Long id);

    void save(User user);

    void deleteUser(long id);
}
