package com.service;

import com.model.Employer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by Ivan Minchev on 10/27/2017.
 */
@Service
public interface EmployerService {

    Page<Employer> listEmployers(Pageable pageable);

    Employer findEmployerById(long id);

    Employer findEmployerByUsername(String username);

    void addEmployer(Employer employer);

    void updateEmployer(Employer employer, String username);

    void deleteEmployer(long id);
}
