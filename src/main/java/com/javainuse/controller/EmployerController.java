package com.javainuse.controller;

import com.javainuse.model.Employee;
import com.javainuse.model.Employer;
import com.javainuse.model.Role;
import com.javainuse.model.User;
import com.javainuse.service.EmployeeService;
import com.javainuse.service.EmployerService;
import com.javainuse.service.EmployerServiceImpl;
import com.javainuse.service.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.javainuse.controller.ControllerConstants.*;
import static com.javainuse.security.SecurityConstants.SECRET;
import static com.javainuse.security.SecurityConstants.TOKEN_PREFIX;

/**
 * Created by Ivan Minchev on 10/27/2017.
 */
@RestController
public class EmployerController {

    @Autowired
    private EmployerService employerData;

    @Autowired
    private EmployeeService employeeData;

    @Autowired
    private UserService userData;


    @RequestMapping(value = LIST_EMPLOYERS_URL, method = RequestMethod.GET)
    public List<Employer> listEmployers(
            @RequestHeader(value="Authorization") String token,
            Pageable pageable) {

        Page<Employer> page = this.employerData.listEmployers(pageable);

        String requestUsername = Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();

        User requestUser = this.userData.findByUsername(requestUsername);
        Role role = (Role) requestUser.getRoles().stream().toArray()[0];

//        if (role.getId() != 1) {
//            throw new IllegalStateException("Only Administrator can see all employers!");
//        }

        return page.getContent();
    }



    @RequestMapping(method = RequestMethod.PUT, value = EDIT_EMPLOYER_URL)
    public void updateEmployer(@PathVariable("id") Long id, @RequestBody Employer payload) {
        Employer employerForEdit = this.employerData.findEmployerById(id);

        if (employerForEdit == null) {
            throw new UsernameNotFoundException("No employer found with id: " + id);
        }
        employerForEdit.setFirstName(payload.getFirstName());
        employerForEdit.setMiddleInitial(payload.getMiddleInitial());
        employerForEdit.setLastName(payload.getLastName());
        employerForEdit.setFirstName(payload.getFirstName());
        this.employerData.updateEmployer(employerForEdit, employerForEdit.getUsername());
    }

    @RequestMapping(method = RequestMethod.POST, value="/employers/{employer_id}/subscribeEmployee/{employee_id}")
    public String subscribeEmployee(
            @PathVariable("employer_id") Long employerId,
            @PathVariable("employee_id") Long employeeId) {

        Employer employer = this.employerData.findEmployerById(employerId);
        Employee employee = this.employeeData.findEmployeeById(employeeId);
        if (employer == null) {
            throw new UsernameNotFoundException("No employer found with username: " + employer.getUsername());
        }
        if (employee == null) {
            throw new UsernameNotFoundException("No employee found with username: " + employee.getUsername());
        }

        /** Checking if current Employee is already subscribed to current Employer*/
        if (employee.getEmployer() != null && employee.getEmployer().getId() == employer.getId()) {
            throw new IllegalStateException(
                    String.format("Employee with username %s was already subscribed to Employer with username %s",
                    employee.getUsername(), employer.getUsername()));
        }


        employee.setEmployer(employer);
        this.employeeData.updateEmployee(employee, employee.getUsername());
        return String.format("Employee with username %s was subscribed to Employer with username %s successfully!!!", employee.getUsername(), employer.getUsername());
    }


    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value="/employers/deleteEmployer/{id}")
    public String deleteEmployer(@PathVariable("id") Long id) {
        Employer employerToBeDeleted = this.employerData.findEmployerById(id);

        if (employerToBeDeleted == null) {
            throw new UsernameNotFoundException("No employer found with id: " + id);
        }

        if (employerToBeDeleted.getEmployees() != null
                && employerToBeDeleted.getEmployees().size() > 0) {
            employerToBeDeleted.getEmployees().forEach(e -> e.setEmployer(null));
        }

        Long userIdToBeDeleted = this.userData.findByUsername(employerToBeDeleted.getUsername()).getUid();

        this.employerData.deleteEmployer(employerToBeDeleted.getId());
        this.userData.deleteUser(userIdToBeDeleted);

        return String.format("Employer with username '%s' was deleted successfully!", employerToBeDeleted.getUsername());
    }


    /**
     * Both methods below are invoked with reflection from UserController.
     */
    public void addNewEmployer (User user) {
        Employer employer = new Employer();
        employer.setUsername(user.getUsername());
        this.employerData.addEmployer(employer);
    }

    private void setEmployerData (EmployerServiceImpl employeeData) {
        this.employerData = employeeData;
    }
}
