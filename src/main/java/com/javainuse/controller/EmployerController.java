package com.javainuse.controller;

import com.javainuse.model.Employee;
import com.javainuse.model.Employer;
import com.javainuse.model.User;
import com.javainuse.service.EmployeeService;
import com.javainuse.service.EmployerService;
import com.javainuse.service.EmployerServiceImpl;
import com.javainuse.service.UserService;
import com.javainuse.validator.EmployerUpdateValidator;
import com.javainuse.validator.UserValidator;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.javainuse.controller.ControllerConstants.*;
import static com.javainuse.security.SecurityConstants.*;

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

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private EmployerUpdateValidator employerUpdateValidator;


    @RequestMapping(value = LIST_ALL_EMPLOYERS_URL, method = RequestMethod.GET)
    public List<Employer> listEmployers(@RequestHeader(value = "Authorization") String authToken,
                                        Pageable pageable) throws IllegalAccessException {

        if (!this.userValidator.isUserInRole(authToken, ROLE_ADMIN)) {
            throw new IllegalAccessException("Only Administrator users can see all employers!");
        }

        Page<Employer> page = this.employerData.listEmployers(pageable);

        return page.getContent();
    }

    @RequestMapping(value = COUNT_EMPLOYEES_URL, method = RequestMethod.GET)
    public long countEmployees(@RequestHeader(value = "Authorization") String authToken,
                               @PathVariable("employer_id") long employerId) throws IllegalAccessException {

        Employer employer = this.employerData.findEmployerById(employerId);

        if (employer == null) {
            throw new UsernameNotFoundException("No employer found with id: " + employerId);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException("Only Administrator or Employer can require count of the Employees!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            if (requestUserId != employerId) {
                throw new IllegalAccessException("An Employer cannot see the count of employees assigned to another Employer!");
            }
        }

        return employer.getEmployees() != null ? employer.getEmployees().size() : 0;
    }

    @RequestMapping(value = LIST_EMPLOYEES_OF_CURRENT_EMPLOYER_URL, method = RequestMethod.GET)
    public List<Employee> listEmployeesOfCurrentEmployer(@RequestHeader("Authorization") String authToken,
                                                         @PathVariable("employer_id") long employerId,
                                                         Pageable pageable) throws IllegalAccessException {

        Employer employer = this.employerData.findEmployerById(employerId);

        if (employer == null) {
            throw new UsernameNotFoundException("No employer found with id: " + employerId);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException("Only Administrator or Employer can see list of employees!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long idRequestUser = this.employerData.findEmployerByUsername(requestUsername).getId();

            if (idRequestUser != employerId) {
                throw new IllegalAccessException("Id of the Employer who sent the request and Employer Id passed in URL do not match!");
            }
        }

        List<Employee> employees = employer.getEmployees().stream().collect(Collectors.toList());
        int start = pageable.getOffset() > employees.size() ? employees.size() : pageable.getOffset();
        int end = (start + pageable.getPageSize()) > employees.size() ? employees.size() : (start + pageable.getPageSize());

        return employees.subList(start, end);
    }



    @RequestMapping(value = UPDATE_EMPLOYER_URL, method = RequestMethod.PUT)
    public String updateEmployer(@RequestHeader("Authorization") String authToken,
                               @PathVariable("id") long id,
                               @RequestBody Employer payload,
                               BindingResult bindingResult) throws IllegalAccessException {

        Employer employerToBeUpdated = this.employerData.findEmployerById(id);

        if (employerToBeUpdated == null) {
            throw new UsernameNotFoundException("No employer found with id: " + id);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException("Only Administrator or Employer can edit Employers!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUser = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUser).getId();

            if (requestUserId != id) {
                throw new IllegalAccessException("Employer cannot edit another Employers!");
            }
        }

        employerToBeUpdated.setFirstName(payload.getFirstName());
        employerToBeUpdated.setMiddleInitial(payload.getMiddleInitial());
        employerToBeUpdated.setLastName(payload.getLastName());

        this.employerUpdateValidator.validate(employerToBeUpdated, bindingResult);
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            return error != null ? error : "Update failed";
        }

        this.employerData.updateEmployer(employerToBeUpdated, employerToBeUpdated.getUsername());

        return String.format("Employer with username '%s' was updated successfully!", employerToBeUpdated.getUsername());
    }


    @Transactional
    @RequestMapping(value = DELETE_EMPLOYER_URL, method = RequestMethod.DELETE)
    public String deleteEmployer(@RequestHeader("Authorization") String authToken,
                                 @PathVariable("id") long id) throws IllegalAccessException {
        Employer employerToBeDeleted = this.employerData.findEmployerById(id);

        if (employerToBeDeleted == null) {
            throw new UsernameNotFoundException("No employer found with id: " + id);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)
                || this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            throw new IllegalAccessException("Only Administrator is able to delete Employers!");
        }

        if (employerToBeDeleted.getEmployees() != null
                && employerToBeDeleted.getEmployees().size() > 0) {
            employerToBeDeleted.getEmployees().forEach(e -> e.setEmployer(null));
        }

        long userIdToBeDeleted = this.userData.findByUsername(employerToBeDeleted.getUsername()).getUid();

        this.employerData.deleteEmployer(employerToBeDeleted.getId());
        this.userData.deleteUser(userIdToBeDeleted);

        return String.format("Employer with username '%s' was deleted successfully!", employerToBeDeleted.getUsername());
    }

    @RequestMapping(value = SUBSCRIBE_EMPLOYEE_URL, method = RequestMethod.POST)
    public String subscribeEmployee(@RequestHeader(value="Authorization") String authToken,
                                    @PathVariable("employer_id") long employerId,
                                    @PathVariable("employee_id") long employeeId) throws IllegalAccessException {

        Employer employer = this.employerData.findEmployerById(employerId);
        Employee employee = this.employeeData.findEmployeeById(employeeId);

        if (employer == null) {
            throw new UsernameNotFoundException("No employer found with Id: " + employerId);
        }
        if (employee == null) {
            throw new UsernameNotFoundException("No employee found with Id: " + employeeId);
        }

        String requestUsername = this.getUsernameRequestUser(authToken);

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException(String.format("Only Employers can assign Employees!"));
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            if (requestUserId != employerId) {
                throw new IllegalAccessException(String.format(
                        "Id of the Employer who sent the request and Employer Id passed in URL do not match!"
                ));
            }

            if (employee.getEmployer() != null) {
                /** Checking if current Employee is already subscribed to current Employer*/
                if (employee.getEmployer().getId() == employer.getId()) {
                    throw new IllegalAccessException(
                            String.format("Employee with username %s is already subscribed to Employer with username %s",
                                    employee.getUsername(), employer.getUsername()));
                } else {
                    throw new IllegalAccessException(
                            String.format("Employee with username %s is subscribed to another Employer!",
                                    employee.getUsername()));
                }
            }
        }

        employee.setEmployer(employer);
        this.employeeData.updateEmployee(employee, employee.getUsername());
        return String.format("Employee with username %s was subscribed to Employer with username %s successfully!!!", employee.getUsername(), employer.getUsername());
    }

    @RequestMapping(value = RELEASE_EMPLOYEE_URL, method = RequestMethod.POST)
    public String releaseEmployee(@RequestHeader("Authorization") String authToken,
                                  @PathVariable("employer_id") long employerId,
                                  @PathVariable("employee_id") long employeeId) throws IllegalAccessException {

        Employer employer = this.employerData.findEmployerById(employerId);
        Employee employee = this.employeeData.findEmployeeById(employeeId);

        if (employer == null) {
            throw new UsernameNotFoundException("No employer found with Id: " + employerId);
        }
        if (employee == null) {
            throw new UsernameNotFoundException("No employee found with Id: " + employeeId);
        }

        String requestUsername = this.getUsernameRequestUser(authToken);

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException("Only Administrator or Employer can release an Employee!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            if (requestUserId != employerId) {
                throw new IllegalAccessException("Id of the Employer who sent the request and Employer Id passed in URL do not match!");
            }

            /** Check if Employee is subscribed to another Employer!*/
            if (employee.getEmployer() != null
                    && employee.getEmployer().getId() != employer.getId()) {
                throw new IllegalAccessException(
                        String.format("Employer is not allowed to release another Employer's Employee!",
                                employee.getUsername(), employer.getUsername()));
            }
        }

        /** Check if Employee has no Employer!*/
        if (employee.getEmployer() == null) {
            throw new IllegalAccessException(String.format(
                    "Employee with username %s has no Employer!", employee.getUsername()));
        }

        employee.setEmployer(null);
        this.employeeData.updateEmployee(employee, employee.getUsername());

        return String.format("Employee with username %s was released successfully!", employee.getUsername());
    }

    @RequestMapping(value = CHANGE_EMPLOYER_STATUS_URL, method = RequestMethod.POST)
    public String changeEmployerStatus(@RequestHeader("Authorization") String authToken,
                                        @PathVariable("id") long id) throws IllegalAccessException {

        Employer employer = this.employerData.findEmployerById(id);

        if (employer == null) {
            throw new UsernameNotFoundException("No employer found with Id: " + id);
        }

        String requestUsername = this.getUsernameRequestUser(authToken);

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException("Only Administrator or Employer can change Employer's status!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();
            if (requestUserId != id) {
                throw new IllegalAccessException("Employer cannot change another Employer's status!");
            }
        }

        employer.setIsActive();
        this.employerData.updateEmployer(employer, employer.getUsername());

        return String.format("Status of Employer with username %s changed to %s!",
                employer.getUsername(), employer.getIsActive() ? "active" : "inactive");
    }


    private String getUsernameRequestUser(String authToken) {
        String usernameRequestUser = Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(authToken.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
        return usernameRequestUser;
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
