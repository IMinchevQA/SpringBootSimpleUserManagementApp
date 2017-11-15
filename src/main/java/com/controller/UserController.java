package com.controller;

import com.model.Employee;
import com.model.Employer;
import com.model.Role;
import com.repository.RoleRepository;
import com.service.*;
import com.validator.UserValidator;
import com.model.User;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.controller.ControllerConstants.*;
import static com.security.SecurityConstants.*;

/**
 * Created by Ivan Minchev on 10/24/2017.
 */
@RestController
public class UserController {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_EMPLOYER = "ROLE_EMPLOYER";
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";

    @Autowired
    private UserService userData;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployerServiceImpl employerData;

    @Autowired
    private EmployeeServiceImpl employeeData;


    @RequestMapping(value = REGISTER_URL, method = RequestMethod.POST)
    public Map<String, String> registerEmployerOrEmployee(@RequestBody RegisterObject payload,
                               BindingResult bindingResult) {

        if (payload.getRoleId() == null) {
            throw new IllegalArgumentException("userForm.roleId is empty!");
        }

        if (payload.getRoleId() == 1) {
            throw new IllegalArgumentException(
                    "Administrator registration must be performed by authenticated Administrator on special URL!");
        }

        Map<String, String> responseObj = new HashMap<>();

        User user = this.registerUser(payload, bindingResult);

        this.userData.save(user);
        createEntity(this.roleRepository.findOne(payload.getRoleId()), user);

        if (payload.getRoleId() == 2) {
            responseObj.put("message", String.format(
                    "%s with username %s registered successfully!",
                    EMPLOYER_STRING,
                    user.getUsername()));

            responseObj.put("employer_id", String.valueOf(
                    this.employerData.findEmployerByUsername(user.getUsername()).getId()));

        } else {
            responseObj.put("message", String.format(
                    "%s with username %s registered successfully!",
                    EMPLOYEE_STRING,
                    user.getUsername()));

            responseObj.put("employee_id", String.valueOf(
                    this.employeeData.findEmployeeByUsername(user.getUsername()).getId()));

        }

        return responseObj;
    }

    @RequestMapping(value = REGISTER_ADMINISTRATOR_URL, method = RequestMethod.POST)
    public Map<String, String> registerAdministrator(@RequestHeader(value = "Authorization") String authToken,
                                                     @RequestBody RegisterObject payload,
                                                     BindingResult bindingResult) {
        if (payload.getRoleId() == null) {
            throw new IllegalArgumentException("userForm.roleId is empty!");
        }

        if (payload.getRoleId() != 1) {
            throw new IllegalArgumentException("Administrator registration requires roleId value 1!");
        }

        if (!this.userValidator.isUserInRole(authToken, ROLE_ADMIN)) {
            throw new UnsupportedOperationException("Only Administrator can register another Administrator!");
        }


        Map<String, String> responseObj = new HashMap<>();

        User user = this.registerUser(payload, bindingResult);

        this.userData.save(user);

        responseObj.put("message", String.format(
                "%s with username %s registered successfully!",
                ADMIN_STRING,
                user.getUsername()));

        responseObj.put("admin_id", String.valueOf(user.getUid()));

        return responseObj;
    }

    private User registerUser(RegisterObject payload, BindingResult bindingResult) {
        User user = new User();
        Set<Role> roles = new HashSet<>();

        roles.add(this.roleRepository.findOne(payload.getRoleId()));
        user.setUsername(payload.getUsername());
        user.setPassword(payload.getPassword());
        user.setPasswordConfirm(payload.getPasswordConfirm());
        user.setRoles(roles);

        this.userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            throw new IllegalArgumentException(error != null ? error : "Registration failed");
        }
        return user;
    }

    @RequestMapping(value = LIST_ALL_USERS_URL, method = RequestMethod.GET)
    public List<User> listUsers(@RequestHeader(value="Authorization") String authToken,
                                Pageable pageable) throws IllegalAccessException {

        if (!this.userValidator.isUserInRole(authToken, ROLE_ADMIN)) {
            throw new UnsupportedOperationException("Only Administrator can see all users!");
        }

        Page<User> page = this.userData.listUsers(pageable);
        return page.getContent();
    }


    private void createEntity(Role role, User user) {
        switch (role.getName()) {
            case ROLE_EMPLOYER:
                Employer employer = new Employer();
                employer.setUsername(user.getUsername());
                this.employerData.addEmployer(employer);
                break;
            case ROLE_EMPLOYEE:
                Employee employee = new Employee();
                employee.setUsername(user.getUsername());
                employee.setDateOfHire(new Date());
                this.employeeData.addEmployee(employee);
                break;
        }
    }
}

class RegisterObject {
    private String username;
    private String password;
    private String passwordConfirm;
    private Long roleId;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return this.passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long role) {
        this.roleId = role;
    }
}
