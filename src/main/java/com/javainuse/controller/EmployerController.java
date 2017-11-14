package com.javainuse.controller;

import com.javainuse.exception.UserNotFoundException;
import com.javainuse.model.Employee;
import com.javainuse.model.Employer;
import com.javainuse.model.User;
import com.javainuse.service.*;
import com.javainuse.validator.EmployerUpdateValidator;
import com.javainuse.validator.UserValidator;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    private TaskService taskData;

    @Autowired
    private UpdateService updateData;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private EmployerUpdateValidator employerUpdateValidator;


    @RequestMapping(value = LIST_ALL_EMPLOYERS_URL, method = RequestMethod.GET)
    public List<Employer> listEmployers(@RequestHeader(value = "Authorization") String authToken,
                                        Pageable pageable) {

        if (!this.userValidator.isUserInRole(authToken, ROLE_ADMIN)) {
            throw new UnsupportedOperationException("Only Administrator users can see all employers!");
        }

        Page<Employer> page = this.employerData.listEmployers(pageable);

        return page.getContent();
    }

    @RequestMapping(value = COUNT_EMPLOYEES_URL, method = RequestMethod.GET)
    public Map<String, String> countEmployees(@RequestHeader(value = "Authorization") String authToken,
                                              @PathVariable("employer_id") long employerId) {

        Employer employer = this.employerData.findEmployerById(employerId);

        if (employer == null) {
            throw new UserNotFoundException(EMPLOYER_STRING, employerId);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new UnsupportedOperationException("Only Administrator or Employer can require count of the Employees!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            if (requestUserId != employerId) {
                throw new UnsupportedOperationException("An Employer cannot see the count of employees assigned to another Employer!");
            }
        }

        Map<String, String> responseObj = new HashMap<>();

        responseObj.put("count_employees", String.valueOf(employer.getEmployees() != null ? employer.getEmployees().size() : 0));

        return responseObj;
    }

    @RequestMapping(value = LIST_EMPLOYEES_OF_CURRENT_EMPLOYER_URL, method = RequestMethod.GET)
    public List<Employee> listEmployeesOfCurrentEmployer(@RequestHeader("Authorization") String authToken,
                                                         @PathVariable("employer_id") long employerId,
                                                         Pageable pageable) {

        Employer employer = this.employerData.findEmployerById(employerId);

        if (employer == null) {
            throw new UserNotFoundException(EMPLOYER_STRING, employerId);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new UnsupportedOperationException("Only Administrator or Employer can see list of employees!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            if (requestUserId != employerId) {
                throw new UnsupportedOperationException("An Employer cannot see employees assigned to another Employer!");
            }
        }

        List<Employee> employees = employer.getEmployees().stream().collect(Collectors.toList());
        int start = pageable.getOffset() > employees.size() ? employees.size() : pageable.getOffset();
        int end = (start + pageable.getPageSize()) > employees.size() ? employees.size() : (start + pageable.getPageSize());

        return employees.subList(start, end);
    }



    @RequestMapping(value = UPDATE_EMPLOYER_URL, method = RequestMethod.PUT)
    public Map<String, String> updateEmployer(@RequestHeader("Authorization") String authToken,
                                              @PathVariable("employer_id") long employerId,
                                              @RequestBody Employer payload,
                                              BindingResult bindingResult) {

        Employer employerToBeUpdated = this.employerData.findEmployerById(employerId);

        if (employerToBeUpdated == null) {
            throw new UserNotFoundException(EMPLOYER_STRING, employerId);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new UnsupportedOperationException("Only Administrator or Employer can edit Employers!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUser = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUser).getId();

            if (requestUserId != employerId) {
                throw new UnsupportedOperationException("Employer cannot update another Employers!");
            }
        }

        employerToBeUpdated.setFirstName(payload.getFirstName());
        employerToBeUpdated.setMiddleInitial(payload.getMiddleInitial());
        employerToBeUpdated.setLastName(payload.getLastName());

        this.employerUpdateValidator.validate(employerToBeUpdated, bindingResult);
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            throw new UnsupportedOperationException(error != null ? error : "Update failed");
        }

        this.employerData.updateEmployer(employerToBeUpdated, employerToBeUpdated.getUsername());

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("message", String.format(
                "Employer with username '%s' updated successfully!",
                employerToBeUpdated.getUsername()));
        responseObj.put("employer_id", String.valueOf(employerId));

        return responseObj;
    }


    @Transactional
    @RequestMapping(value = DELETE_EMPLOYER_URL, method = RequestMethod.DELETE)
    public Map<String, String> deleteEmployer(@RequestHeader("Authorization") String authToken,
                                              @PathVariable("employer_id") long employerId) {

        Employer employerToBeDeleted = this.employerData.findEmployerById(employerId);

        if (employerToBeDeleted == null) {
            throw new UserNotFoundException(EMPLOYER_STRING, employerId);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)
                || this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            throw new UnsupportedOperationException("Only Administrator is able to delete Employers!");
        }

        /**
         * Clear each Employee's field - Employer;
         */
        if (employerToBeDeleted.getEmployees() != null
                && employerToBeDeleted.getEmployees().size() > 0) {
            employerToBeDeleted.getEmployees().forEach(e -> {
                e.setEmployer(null);
                /**
                 * Clear each Employee's field - Tasks;
                 */
                if (e.getEmployeeTasks() != null && e.getEmployeeTasks().size() > 0) {
                    e.setEmployeeTasks(new HashSet<>());
                }
            });
        }

        /**
         * Remove all tasks created by the Employer and corresponding Task progress updates.
         */
        if (employerToBeDeleted.getTaskEmployees() != null
                && employerToBeDeleted.getTaskEmployees().size() > 0) {
            employerToBeDeleted.getTaskEmployees().forEach(t -> {
                if (t.getUpdates() != null) {
                    t.getUpdates().forEach(u -> this.updateData.deleteUpdate(u.getId()));
                }
                this.taskData.deleteTask(t.getTid());
            });
        }

        long userIdToBeDeleted = this.userData.findByUsername(employerToBeDeleted.getUsername()).getUid();

        this.employerData.deleteEmployer(employerToBeDeleted.getId());
        this.userData.deleteUser(userIdToBeDeleted);

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("message", String.format(
                "Employer with username '%s' deleted successfully!",
                employerToBeDeleted.getUsername()));
        responseObj.put("employer_id", String.valueOf(employerId));

        return responseObj;
    }

    @RequestMapping(value = SUBSCRIBE_EMPLOYEE_URL, method = RequestMethod.POST)
    public Map<String, String> subscribeEmployee(@RequestHeader(value="Authorization") String authToken,
                                                 @PathVariable("employer_id") long employerId,
                                                 @PathVariable("employee_id") long employeeId) {

        Employer employer = this.employerData.findEmployerById(employerId);
        Employee employee = this.employeeData.findEmployeeById(employeeId);

        if (employer == null) {
            throw new UserNotFoundException(EMPLOYER_STRING, employerId);
        }
        if (employee == null) {
            throw new UserNotFoundException(EMPLOYEE_STRING ,employeeId);
        }


        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new UnsupportedOperationException("Only Employers can assign Employees!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            if (requestUserId != employerId) {
                throw new UnsupportedOperationException("An Employer cannot subscribe Employee to another Employer!");
            }

            if (employee.getEmployer() != null) {
                /** Checking if current Employee is already subscribed to current Employer*/
                if (employee.getEmployer().getId() == employer.getId()) {
                    throw new UnsupportedOperationException(
                            String.format(
                                    "Employee with username %s is already subscribed to Employer with username %s",
                                    employee.getUsername(),
                                    employer.getUsername()));
                } else {
                    throw new UnsupportedOperationException(
                            String.format(
                                    "Employee with username %s is subscribed to another Employer!",
                                    employee.getUsername()));
                }
            }
        }

        employee.setEmployer(employer);
        this.employeeData.updateEmployee(employee, employee.getUsername());

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("message", String.format(
                "Employee with username %s was subscribed to Employer with username %s successfully!!!",
                employee.getUsername(),
                employer.getUsername()));
        responseObj.put("employer_id", String.valueOf(employerId));
        responseObj.put("employee_id", String.valueOf(employeeId));

        return responseObj;
    }

    @RequestMapping(value = RELEASE_EMPLOYEE_URL, method = RequestMethod.POST)
    public Map<String, String> releaseEmployee(@RequestHeader("Authorization") String authToken,
                                               @PathVariable("employer_id") long employerId,
                                               @PathVariable("employee_id") long employeeId) {

        Employer employer = this.employerData.findEmployerById(employerId);
        Employee employee = this.employeeData.findEmployeeById(employeeId);

        if (employer == null) {
            throw new UserNotFoundException(EMPLOYER_STRING, employeeId);
        }
        if (employee == null) {
            throw new UserNotFoundException(EMPLOYEE_STRING, employeeId);
        }

        String requestUsername = this.getUsernameRequestUser(authToken);

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new UnsupportedOperationException("Only Administrator or Employer can release an Employee!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            if (requestUserId != employerId) {
                throw new UnsupportedOperationException("Id of the Employer who sent the request and Employer Id passed in URL do not match!");
            }

            /** Check if Employee is subscribed to another Employer!*/
            if (employee.getEmployer() != null
                    && employee.getEmployer().getId() != employer.getId()) {
                throw new UnsupportedOperationException(
                        String.format(
                                "Employer is not allowed to release another Employer's Employee!",
                                employee.getUsername(),
                                employer.getUsername()));
            }
        }

        /** Check if Employee has no Employer!*/
        if (employee.getEmployer() == null) {
            throw new UnsupportedOperationException(
                    String.format(
                            "Employee with username %s has no Employer!",
                            employee.getUsername()));
        }

        employee.setEmployer(null);
        this.employeeData.updateEmployee(employee, employee.getUsername());

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("message", String.format("Employee with username %s was released successfully!", employee.getUsername()));
        responseObj.put("employee_id", String.valueOf(employeeId));

        return responseObj;
    }

    @RequestMapping(value = CHANGE_EMPLOYER_STATUS_URL, method = RequestMethod.POST)
    public Map<String, String> changeEmployerStatus(@RequestHeader("Authorization") String authToken,
                                                    @PathVariable("employer_id") long employerId) {

        Employer employer = this.employerData.findEmployerById(employerId);

        if (employer == null) {
            throw new UserNotFoundException(EMPLOYER_STRING, employerId);
        }


        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new UnsupportedOperationException("Only Administrator or Employer can change Employer's status!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            if (requestUserId != employerId) {
                throw new UnsupportedOperationException("An Employer cannot change the status of another Employer!");
            }
        }

        employer.setIsActive();
        this.employerData.updateEmployer(employer, employer.getUsername());

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("message", String.format(
                "Status of Employer with username %s changed to %s!",
                employer.getUsername(),
                employer.getIsActive() ? "active" : "inactive"));
        responseObj.put("employer_id", String.valueOf(employerId));

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
