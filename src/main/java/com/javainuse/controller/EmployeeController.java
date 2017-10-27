package com.javainuse.controller;

import com.javainuse.model.User;
import com.javainuse.service.EmployeeServiceImpl;
import com.javainuse.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by Ivan Minchev on 10/18/2017.
 */

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeData;


    @RequestMapping(value = "/employees/listEmployees", method = RequestMethod.GET)
    public List<Employee> listEmployees() {
        return this.employeeData.getAllEmployees();
    }


    public void addNewEmployee(User user) {
        Employee employee = new Employee();
        employee.setUsername(user.getUsername());
        this.employeeData.addEmployee(employee);
    }

    @RequestMapping(method = RequestMethod.PUT, value="/employees/editEmployee/{username}")
    public void updateEmployee(@PathVariable String username, @RequestBody Employee payload) {
        Employee employeeForEdit = this.employeeData.findEmployeeByUsername(username);

        if (employeeForEdit == null) {
            throw new UsernameNotFoundException("No employee found with username: " + username);
        }

        employeeForEdit.setFirstName(payload.getFirstName());
        employeeForEdit.setMiddleInitial(payload.getMiddleInitial());
        employeeForEdit.setLastName(payload.getLastName());
        employeeForEdit.setEmployeeNumber(payload.getEmployeeNumber());
        employeeForEdit.setDepartmentID(payload.getDepartmentID());
        employeeForEdit.setPhoneNumber(payload.getPhoneNumber());
        employeeForEdit.setDateOfHire(payload.getDateOfHire());
        this.employeeData.updateEmployee(employeeForEdit, username);
    }


    @RequestMapping(method = RequestMethod.DELETE, value="/employees/deleteEmployee/{username}")
    public void deleteEmployee(@PathVariable String username) {
        Employee employeeToBeDeleted = this.employeeData.findEmployeeByUsername(username);
        if (employeeToBeDeleted == null) {
            throw new UsernameNotFoundException("No employee found with username: " + username);
        }

        this.employeeData.deleteEmployee(employeeToBeDeleted.getId());
    }

    private void setEmployeeData (EmployeeServiceImpl employeeData) {
        this.employeeData = employeeData;
    }
}
