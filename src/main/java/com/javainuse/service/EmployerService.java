package com.javainuse.service;

import com.javainuse.model.Employer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Ivan Minchev on 10/27/2017.
 */
@Service
public interface EmployerService {

    List<Employer> getAllEmployers();

    Employer findEmployerById(long id);

    Employer findEmployerByUsername(String username);

    void addEmployer(Employer employer);

    void updateEmployer(Employer employer, String username);

    void deleteEmployer(long id);
}