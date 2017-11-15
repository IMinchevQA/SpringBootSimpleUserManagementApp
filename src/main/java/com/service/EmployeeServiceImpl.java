package com.service;

import com.repository.EmployeeRepository;
import com.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan Minchev on 10/23/2017.
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public Page<Employee> listEmployees(Pageable pageable) {
        return this.employeeRepository.findAll(pageable);
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> allEmployees = new ArrayList<>();
        this.employeeRepository.findAll().forEach(allEmployees::add);
        return allEmployees;
    }

    @Override
    public Employee findEmployeeById(long id) {
        return this.employeeRepository.findOne(id);
    }

    @Override
    public Employee findEmployeeByUsername(String username) {
        return this.employeeRepository.findEmployeeByUsername(username);
    }

    @Override
    public void addEmployee(Employee employee) {
        this.employeeRepository.save(employee);
    }

    @Override
    public void updateEmployee(Employee employee, String username) {
        this.employeeRepository.save(employee);
    }

    public void deleteEmployee(long id) {
        this.employeeRepository.delete(id);
    }
}
