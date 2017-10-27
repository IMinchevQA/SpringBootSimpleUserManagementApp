package com.javainuse.controller;

import com.javainuse.model.Employee;
import com.javainuse.model.Employer;
import com.javainuse.model.Role;
import com.javainuse.model.User;
import com.javainuse.service.EmployeeServiceImpl;
import com.javainuse.service.EmployerService;
import com.javainuse.service.EmployerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by Ivan Minchev on 10/27/2017.
 */
@RestController
public class EmployerController {

    @Autowired
    private EmployerServiceImpl employerData;

    @Autowired
    private EmployeeServiceImpl employeeData;

//    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value="listEmployers", method = RequestMethod.GET)
    public List<Employer> listEmployers(@AuthenticationPrincipal User currentUser) {
        Role role = (Role) currentUser.getRoles();
        return this.employerData.getAllEmployers();
    }

    public void addNewEmployer (User user) {
        Employer employer = new Employer();
        employer.setUsername(user.getUsername());
        this.employerData.addEmployer(employer);
    }

    @RequestMapping(method = RequestMethod.PUT, value="/employers/editEmployer/{username}")
    public void updateEmployer(@PathVariable String username, @RequestBody Employer payload) {
        Employer employerForEdit = this.employerData.findEmployerByUsername(username);

        if (employerForEdit == null) {
            throw new UsernameNotFoundException("No employer found with username: " + username);
        }
        employerForEdit.setFirstName(payload.getFirstName());
        employerForEdit.setMiddleInitial(payload.getMiddleInitial());
        employerForEdit.setLastName(payload.getLastName());
        employerForEdit.setFirstName(payload.getFirstName());
        this.employerData.updateEmployer(employerForEdit, username);
    }

    @RequestMapping(method = RequestMethod.POST, value="/employers/{employerUsername}/subscribeEmployee/{employeeUsername}")
    public String subscribeEmployee(
            @PathVariable("employerUsername") String employerUsername,
            @PathVariable("employeeUsername") String employeeUsername) {

        Employer employer = this.employerData.findEmployerByUsername(employerUsername);
        Employee employee = this.employeeData.findEmployeeByUsername(employeeUsername);
        if (employer == null) {
            throw new UsernameNotFoundException("No employer found with username: " + employerUsername);
        }
        if (employee == null) {
            throw new UsernameNotFoundException("No employee found with username: " + employeeUsername);
        }

        /** Checking if current Employee is already subscribed at current Employer*/
        if (employee.getEmployer().getId() == employer.getId()) {
            throw new IllegalStateException("Employee with username "
                    + employeeUsername
                    + "was already subscribed to Employer with username "
                    + employerUsername);
        }


        employee.setEmployer(employer);
        this.employeeData.updateEmployee(employee, employeeUsername);

        return "Employee with username "
                + employeeUsername
                + " was subscribed to Employer with username "
                + employerUsername
                + " successfully!!!";
    }


    @RequestMapping(method = RequestMethod.DELETE, value="/employers/deleteEmployer/{username}")
    public void deleteEmployer(@PathVariable("username") String username) {
        Employer employerToBeDeleted = this.employerData.findEmployerByUsername(username);
        if (employerToBeDeleted == null) {
            throw new UsernameNotFoundException("No employer found with username: " + username);
        }

        this.employerData.deleteEmployer(employerToBeDeleted.getId());
    }

    private void setEmployerData (EmployerServiceImpl employeeData) {
        this.employerData = employeeData;
    }
}
