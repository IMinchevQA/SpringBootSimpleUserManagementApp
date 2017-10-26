package com.javainuse.service;

import com.javainuse.repository.EmployeeRepository;
import com.javainuse.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        this.employeeRepository.findAll().forEach(employees::add);
        return employees;
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

//    public void updateEmployee(long id, Employee employee) {
//        this.employeeRepository.save(employee);
//    }

    public void deleteEmployee(long id) {
        this.employeeRepository.delete(id);
    }
}
