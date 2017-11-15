package com.repository;

import com.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Ivan Minchev on 10/24/2017.
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    User findUserByUsername(String username);
}
