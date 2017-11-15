package com.repository;

import com.model.Employer;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Ivan Minchev on 10/27/2017.
 */
public interface EmployerRepository extends PagingAndSortingRepository<Employer, Long> {
    Employer findEmployerByUsername(String username);
}
