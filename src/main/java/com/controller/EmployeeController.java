package com.controller;

import com.exception.UserNotFoundException;
import com.model.Employer;
import com.service.EmployeeService;
import com.model.Employee;
import com.service.EmployerService;
import com.service.UserService;
import com.validator.EmployeeByEmployeeUpdateValidator;
import com.validator.EmployeeByEmployerUpdateValidator;
import com.validator.UserValidator;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.controller.ControllerConstants.*;
import static com.security.SecurityConstants.SECRET;
import static com.security.SecurityConstants.TOKEN_PREFIX;


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
            throw new UnsupportedOperationException("Only Administrator users can see all employees!");
        }

        Page<Employee> page = this.employeeData.listEmployees(pageable);
        return page.getContent();
    }

    @RequestMapping(value = UPDATE_EMPLOYEE_URL, method = RequestMethod.PUT)
    public Map<String, String> updateEmployee(@RequestHeader(value="Authorization") String authToken,
                                              @PathVariable("employee_id") long employeeId,
                                              @RequestBody Employee payload,
                                              BindingResult bindingResult) {

        Employee employeeToBeUpdated = this.employeeData.findEmployeeById(employeeId);

        if (employeeToBeUpdated == null) {
            throw new UserNotFoundException(EMPLOYEE_STRING, employeeId);
        }

        String usernameRequestUser = getUsernameRequestUser(authToken);
        Map<String, String> responseObj = new HashMap<>();

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            Employee requestEmployee = this.employeeData.findEmployeeByUsername(usernameRequestUser);

            /**
             * Checking if request Employee tries to modify data to another Employee
             */
            if (requestEmployee.getId() != employeeId) {
                throw new UnsupportedOperationException("Employee users can only update their own profile data!");
            }

            responseObj.put("message", updateEmployeeByEmployee(employeeToBeUpdated, payload, bindingResult));
            responseObj.put("employee_id", String.valueOf(employeeToBeUpdated.getId()));
            return responseObj;
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            Employer requestEmployer = this.employerData.findEmployerByUsername(usernameRequestUser);

            Employer employeeEmployer = employeeToBeUpdated.getEmployer();

            /**
             * Check if current Employee has Employer.
             */
            if (employeeEmployer == null) {
                throw new UnsupportedOperationException("Current Employee has no Employer. The Employee must be assigned to an Employer first and then might be updated!");
            }

            /**
             * Check if the Employee is assigned to the request Employer.
             */
            if (requestEmployer.getId() != employeeEmployer.getId()) {
                throw new UnsupportedOperationException("An Employer cannot update Employee assigned to another Employer!");
            }
            responseObj.put("message", updateEmployeeByEmployer(employeeToBeUpdated, payload, bindingResult));
            responseObj.put("employee_id", String.valueOf(employeeToBeUpdated.getId()));
            return responseObj;
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
            throw new UnsupportedOperationException(error != null ? error : "Update failed");
        }

        this.employeeData.updateEmployee(employeeToBeUpdated, employeeToBeUpdated.getUsername());

        responseObj.put("message", String.format("Employee with username '%s' was updated successfully!", employeeToBeUpdated.getUsername()));
        responseObj.put("employee_id", String.valueOf(employeeToBeUpdated.getId()));

        return responseObj;
    }

    @RequestMapping(value = DELETE_EMPLOYEE_URL, method = RequestMethod.DELETE)
    public Map<String, String> deleteEmployee(@RequestHeader(value="Authorization") String authToken,
                                              @PathVariable("employee_id") long employeeId) {

        Employee employeeToBeDeleted = this.employeeData.findEmployeeById(employeeId);

        if (employeeToBeDeleted == null) {
            throw new UserNotFoundException(EMPLOYEE_STRING, employeeId);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new UnsupportedOperationException("Only Administrator or Employer user can delete an Employee!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            /**
             * Check if current Employee has Employer.
             */
            if (employeeToBeDeleted.getEmployer() == null) {
                throw new UnsupportedOperationException("Current Employee has no Employer. Only Administrator can delete unsigned Employee!");
            }

            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestEmployerId = this.employerData.findEmployerByUsername(requestUsername).getId();

            /**
             * Check if the Employee is assigned to the request Employer.
             */
            if (requestEmployerId != employeeToBeDeleted.getEmployer().getId()) {
                throw new UnsupportedOperationException("An Employer cannot delete Employee assigned to another Employer!");
            }
        }

        long userIdToBeDeleted = this.userData.findByUsername(employeeToBeDeleted.getUsername()).getUid();

        this.employeeData.deleteEmployee(employeeToBeDeleted.getId());
        this.userData.deleteUser(userIdToBeDeleted);

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("message",  String.format("Employee and User with username '%s' was deleted successfully!", employeeToBeDeleted.getUsername()));
        responseObj.put("employee_id", String.valueOf(employeeId));

        return responseObj;
    }

    private String updateEmployeeByEmployer(Employee employeeToBeUpdated,
                                            Employee payload,
                                            BindingResult bindingResult) {
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

            throw new UnsupportedOperationException("Employer is not allowed to modify: first name, middle initial, last name, phone number, date of hire, sex, date of birth!");
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
            throw new UnsupportedOperationException(error != null ? error : "Update failed");
        }
        this.employeeData.updateEmployee(employeeToBeUpdated, employeeToBeUpdated.getUsername());

        return String.format("Employee with username '%s' was updated successfully!", employeeToBeUpdated.getUsername());
    }

    private String updateEmployeeByEmployee(Employee employeeToBeUpdated,
                                            Employee payload,
                                            BindingResult bindingResult) {

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

            throw new UnsupportedOperationException("Employee is not allowed to modify: employee number, working department, date of hire, job, education level, salary, bonus and commission!");
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
            throw new UnsupportedOperationException(error != null ? error : "Update failed");
        }

        this.employeeData.updateEmployee(employeeToBeUpdated, employeeToBeUpdated.getUsername());
        return String.format("Employee with username '%s' was updated successfully!", employeeToBeUpdated.getUsername());
    }

    @RequestMapping(value = CHANGE_EMPLOYEE_STATUS_URL, method = RequestMethod.POST)
    private Map<String, String> changeEmployeeStatus(@RequestHeader("Authorization") String authToken,
                                                     @PathVariable("employee_id") long employeeId) {

        Employee employee = this.employeeData.findEmployeeById(employeeId);

        if (employee == null) {
            throw new UserNotFoundException(EMPLOYEE_STRING, employeeId);
        }

        String requestUsername = this.getUsernameRequestUser(authToken);
        long requestUserId;

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            requestUserId = this.employeeData.findEmployeeByUsername(requestUsername).getId();
            if (requestUserId != employeeId) {
                throw new UnsupportedOperationException("Employee cannot change another Employee's status!");
            }
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();
            if (employee.getEmployer() != null && requestUserId != employee.getEmployer().getId()) {
                throw new UnsupportedOperationException("Employer cannot change the status of Employee subscribed to another Employer!");
            } else if (employee.getEmployer() == null) {
                throw new UnsupportedOperationException("Only Administrator can change status of Employee who has no Employer!");
            }
        }

        employee.setIsActive();
        this.employeeData.updateEmployee(employee, employee.getUsername());

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("message", String.format("Status of Employee with username %s changed to %s!",
                employee.getUsername(), employee.getIsActive() ? "active" : "inactive"));
        responseObj.put("employee_id", String.valueOf(employeeId));

        return responseObj;
    }

    private String getUsernameRequestUser(String authToken) {
        String usernameRequestUser = Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(authToken.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
        return usernameRequestUser;
    }
}
