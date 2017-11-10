package com.javainuse.controller;

import com.javainuse.model.Employer;
import com.javainuse.model.User;
import com.javainuse.service.EmployeeService;
import com.javainuse.model.Employee;
import com.javainuse.service.EmployeeServiceImpl;
import com.javainuse.service.EmployerService;
import com.javainuse.service.UserService;
import com.javainuse.validator.EmployeeByEmployeeUpdateValidator;
import com.javainuse.validator.EmployeeByEmployerUpdateValidator;
import com.javainuse.validator.UserValidator;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.javainuse.controller.ControllerConstants.*;
import static com.javainuse.security.SecurityConstants.SECRET;
import static com.javainuse.security.SecurityConstants.TOKEN_PREFIX;


/**
 * Created by Ivan Minchev on 10/18/2017.
 */

@RestController
public class EmployeeController {

    @Autowired
    private UserService userData;

    @Autowired
    private EmployerService employerData;

    @Autowired
    private EmployeeService employeeData;


    @Autowired
    private UserValidator userValidator;

    @Autowired
    private EmployeeByEmployeeUpdateValidator employeeByEmployeeUpdateValidator;

    @Autowired
    private EmployeeByEmployerUpdateValidator employeeByEmployerUpdateValidator;


    @RequestMapping(value = LIST_ALL_EMPLOYEES_URL, method = RequestMethod.GET)
    public List<Employee> listEmployees(@RequestHeader(value="Authorization") String authToken,
                                        Pageable pageable) throws IllegalAccessException {

        if (!this.userValidator.isUserInRole(authToken, ROLE_ADMIN)) {
            throw new IllegalAccessException("Only Administrator users can see all employees!");
        }

        Page<Employee> page = this.employeeData.listEmployees(pageable);
        return page.getContent();
    }

    @RequestMapping(value = UPDATE_EMPLOYEE_URL, method = RequestMethod.PUT)
    public String updateEmployee(@RequestHeader(value="Authorization") String authToken,
                                 @PathVariable("id") long id,
                                 @RequestBody Employee payload,
                                 BindingResult bindingResult) throws IllegalAccessException {

        Employee employeeToBeUpdated = this.employeeData.findEmployeeById(id);

        if (employeeToBeUpdated == null) {
            throw new UsernameNotFoundException("There is no employee with id: " + id);
        }

        String usernameRequestUser = getUsernameRequestUser(authToken);

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            Employee requestEmployee = this.employeeData.findEmployeeByUsername(usernameRequestUser);

            /**
             * Checking if request Employee tries to modify data to another Employee
             */
            if (requestEmployee.getId() != id) {
                throw new IllegalAccessException("Employee users can only update their own profile data!");
            }

            return updateEmployeeByEmployee(employeeToBeUpdated, payload, bindingResult);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            Employer requestEmployer = this.employerData.findEmployerByUsername(usernameRequestUser);

            Employer employeeEmployer = employeeToBeUpdated.getEmployer();

            /**
             * Check if current Employee has Employer.
             */
            if (employeeEmployer == null) {
                throw new IllegalAccessException("Current Employee has no Employer. The Employee must be assigned to an Employer first and then might be updated!");
            }

            /**
             * Check if the Employee is assigned to the request Employer.
             */
            if (requestEmployer.getId() != employeeEmployer.getId()) {
                throw new IllegalAccessException("An Employer cannot update Employee assigned to another Employer!");
            }
            return updateEmployeeByEmployer(employeeToBeUpdated, payload, bindingResult);
        }

        /**
         * When user is Admin a full set off values must be provided.
         */
        employeeToBeUpdated.setEmployeeNumber(payload.getEmployeeNumber());
        employeeToBeUpdated.setFirstName(payload.getFirstName());
        employeeToBeUpdated.setMiddleInitial(payload.getMiddleInitial());
        employeeToBeUpdated.setLastName(payload.getLastName());
        employeeToBeUpdated.setDepartmentID(payload.getDepartmentID());
        employeeToBeUpdated.setPhoneNumber(payload.getPhoneNumber());
        employeeToBeUpdated.setDateOfHire(payload.getDateOfHire());
        employeeToBeUpdated.setJob(payload.getJob());
        employeeToBeUpdated.setFormalEducationYears(payload.getFormalEducationYears());
        employeeToBeUpdated.setSex(payload.getSex());
        employeeToBeUpdated.setDateOfBirth(payload.getDateOfBirth());
        employeeToBeUpdated.setYearSalary(payload.getYearSalary());
        employeeToBeUpdated.setYearBonus(payload.getYearBonus());
        employeeToBeUpdated.setCommission(payload.getCommission());

        /**
         * Performing full UpdateTaskProgress validation
         */
        this.employeeByEmployeeUpdateValidator.validate(employeeToBeUpdated, bindingResult);
        this.employeeByEmployerUpdateValidator.validate(employeeToBeUpdated, bindingResult);


        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            return error != null ? error : "UpdateTaskProgress failed";
        }

        this.employeeData.updateEmployee(employeeToBeUpdated, employeeToBeUpdated.getUsername());

        return String.format("Employee with username '%s' was updated successfully!", employeeToBeUpdated.getUsername());
    }

    @RequestMapping(value = DELETE_EMPLOYEE_URL, method = RequestMethod.DELETE)
    public String deleteEmployee(@RequestHeader(value="Authorization") String authToken,
                                 @PathVariable("id") long id) throws IllegalAccessException {

        Employee employeeToBeDeleted = this.employeeData.findEmployeeById(id);

        if (employeeToBeDeleted == null) {
            throw new UsernameNotFoundException("There is no employee with id: " + id);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException("Only Administrator or Employer users is able to delete an Employee!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            /**
             * Check if current Employee has Employer.
             */
            if (employeeToBeDeleted.getEmployer() == null) {
                throw new IllegalAccessException("Current Employee has no Employer. Only Administrator is able to delete unsigned Employees!");
            }

            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestEmployerId = this.employerData.findEmployerByUsername(requestUsername).getId();

            /**
             * Check if the Employee is assigned to the request Employer.
             */
            if (requestEmployerId != employeeToBeDeleted.getEmployer().getId()) {
                throw new IllegalAccessException("An Employer cannot delete Employee assigned to another Employer!");
            }
        }

        long userIdToBeDeleted = this.userData.findByUsername(employeeToBeDeleted.getUsername()).getUid();

        this.employeeData.deleteEmployee(employeeToBeDeleted.getId());
        this.userData.deleteUser(userIdToBeDeleted);

        return String.format("Employee and User with username '%s' was deleted successfully!", employeeToBeDeleted.getUsername());
    }

    private String updateEmployeeByEmployer(Employee employeeToBeUpdated, Employee payload, BindingResult bindingResult) throws IllegalAccessException {
        /**
         * Check if Employer tries to edit some of the FORBIDDEN fields:
         * first name
         * middle initial
         * last name
         * phone number
         * date of hire
         * sex
         * date of birth
         */
        if (payload.getFirstName() != null
                || payload.getMiddleInitial() != null
                || payload.getLastName() != null
                || payload.getPhoneNumber() != null
                || payload.getDateOfHire() != null
                || payload.getSex() != null
                || payload.getDateOfBirth() != null) {

            throw new IllegalAccessException("Employer is not allowed to modify: first name, middle initial, last name, phone number, date of hire, sex, date of birth!");
        }

        employeeToBeUpdated.setEmployeeNumber(payload.getEmployeeNumber());
        employeeToBeUpdated.setDepartmentID(payload.getDepartmentID());
        employeeToBeUpdated.setFormalEducationYears(payload.getFormalEducationYears());
        employeeToBeUpdated.setJob(payload.getJob());
        employeeToBeUpdated.setYearSalary(payload.getYearSalary());
        employeeToBeUpdated.setYearBonus(payload.getYearBonus());
        employeeToBeUpdated.setCommission(payload.getCommission());

        this.employeeByEmployerUpdateValidator.validate(employeeToBeUpdated, bindingResult);
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            return error != null ? error : "UpdateTaskProgress failed";
        }
        this.employeeData.updateEmployee(employeeToBeUpdated, employeeToBeUpdated.getUsername());

        return String.format("Employee with username '%s' was updated successfully!", employeeToBeUpdated.getUsername());
    }

    private String updateEmployeeByEmployee(Employee employeeToBeUpdated, Employee payload, BindingResult bindingResult) throws IllegalAccessException {

        /**
         * Check if Employee tries to edit some of the FORBIDDEN fields:
         * employee number
         * working department
         * date of hire
         * job
         * education level
         * salary
         * bonus
         * commission
         */
        if (payload.getEmployeeNumber() != null
                || payload.getDepartmentID() != null
                || payload.getDateOfHire() != null
                || payload.getJob() != null
                || payload.getFormalEducationYears() != null
                || payload.getYearSalary() != null
                || payload.getYearBonus() != null
                || payload.getCommission() != null) {

            throw new IllegalAccessException("Employee is not allowed to modify: employee number, working department, date of hire, job, education level, salary, bonus and commission!");
        }

        employeeToBeUpdated.setFirstName(payload.getFirstName());
        employeeToBeUpdated.setMiddleInitial(payload.getMiddleInitial());
        employeeToBeUpdated.setLastName(payload.getLastName());
        employeeToBeUpdated.setPhoneNumber(payload.getPhoneNumber());
        employeeToBeUpdated.setSex(payload.getSex());
        employeeToBeUpdated.setDateOfBirth(payload.getDateOfBirth());

        this.employeeByEmployeeUpdateValidator.validate(employeeToBeUpdated, bindingResult);
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            return error != null ? error : "UpdateTaskProgress failed";
        }

        this.employeeData.updateEmployee(employeeToBeUpdated, employeeToBeUpdated.getUsername());
        return String.format("Employee with username '%s' was updated successfully!", employeeToBeUpdated.getUsername());
    }

    @RequestMapping(value = CHANGE_EMPLOYEE_STATUS_URL, method = RequestMethod.POST)
    private String changeEmployeeStatus(@RequestHeader("Authorization") String authToken,
                                        @PathVariable("id") long id) throws IllegalAccessException {

        Employee employee = this.employeeData.findEmployeeById(id);

        if (employee == null) {
            throw new UsernameNotFoundException("There is no employee with id: " + id);
        }

        String requestUsername = this.getUsernameRequestUser(authToken);
        long requestUserId;

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            requestUserId = this.employeeData.findEmployeeByUsername(requestUsername).getId();
            if (requestUserId != id) {
                throw new IllegalAccessException("Employee cannot change another Employee's status!");
            }
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();
            if (employee.getEmployer() != null && requestUserId != employee.getEmployer().getId()) {
                throw new IllegalAccessException("Employer cannot change the status of Employee subscribed to another Employer!");
            } else if (employee.getEmployer() == null) {
                throw new IllegalAccessException("Only Administrator can change status of Employee who has no Employer!");
            }
        }

        employee.setIsActive();
        this.employeeData.updateEmployee(employee, employee.getUsername());

        return String.format("Status of Employee with username %s changed to %s!",
                employee.getUsername(), employee.getIsActive() ? "active" : "inactive");
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
    public void addNewEmployee(User user) {
        Employee employee = new Employee();
        employee.setUsername(user.getUsername());
        employee.setDateOfHire(new Date());
        this.employeeData.addEmployee(employee);
    }

    private void setEmployeeData (EmployeeServiceImpl employeeData) {
        this.employeeData = employeeData;
    }
}
