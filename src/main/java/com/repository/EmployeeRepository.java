package com.repository;

import com.model.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Ivan Minchev on 10/18/2017.
 */
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {
    Employee findEmployeeByUsername(String username);
}
