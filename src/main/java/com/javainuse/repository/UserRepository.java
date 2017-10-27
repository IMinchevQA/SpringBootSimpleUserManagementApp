package com.javainuse.repository;

import com.javainuse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ivan Minchev on 10/24/2017.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
