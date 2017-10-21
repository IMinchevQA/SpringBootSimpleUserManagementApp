package com.javainuse.data;

import com.javainuse.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ivan Minchev on 10/18/2017.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}