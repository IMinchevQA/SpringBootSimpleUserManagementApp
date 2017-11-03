package com.javainuse.service;

import com.javainuse.model.Employer;
import com.javainuse.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan Minchev on 10/27/2017.
 */
@Service
public class EmployerServiceImpl implements EmployerService {

    @Autowired
    EmployerRepository employerRepository;

    @Override
    public Page<Employer> listEmployers(Pageable pageable) {
        return this.employerRepository.findAll(pageable);
    }

    @Override
    public Employer findEmployerById(long id) {
        return this.employerRepository.findOne(id);
    }

    @Override
    public Employer findEmployerByUsername(String username) {
        return this.employerRepository.findEmployerByUsername(username);
    }

    @Override
    public void addEmployer(Employer employer) {
        this.employerRepository.save(employer);
    }

    @Override
    public void updateEmployer(Employer employer, String username) {
        this.employerRepository.save(employer);
    }

    @Override
    public void deleteEmployer(long id) {
        this.employerRepository.delete(id);
    }
}
