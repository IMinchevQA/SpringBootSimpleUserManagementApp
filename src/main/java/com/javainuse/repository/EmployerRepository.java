package com.javainuse.repository;

import com.javainuse.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ivan Minchev on 10/27/2017.
 */
public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Employer findEmployerByUsername(String username);
}
