package com.javainuse.service;

import com.javainuse.model.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Ivan Minchev on 10/26/2017.
 */
@Service
public interface EmployeeService {

    List<Employee> getAllEmployees();

    Employee findEmployeeById(long id);

    Employee findEmployeeByUsername(String username);

    void addEmployee(Employee employee);

    void updateEmployee(Employee employee, String username);

    void deleteEmployee(long id);
}
