package com.javainuse.controller;

import com.javainuse.model.Employee;
import com.javainuse.model.Employer;
import com.javainuse.model.Role;
import com.javainuse.repository.RoleRepository;
import com.javainuse.service.*;
import com.javainuse.validator.UserValidator;
import com.javainuse.model.User;

import io.jsonwebtoken.Jwts;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.javainuse.controller.ControllerConstants.*;
import static com.javainuse.security.SecurityConstants.*;

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



    @RequestMapping(value = LIST_ALL_USERS_URL, method = RequestMethod.GET)
    public List<User> listUsers(@RequestHeader(value="Authorization") String authToken,
                                Pageable pageable) throws IllegalAccessException {
        String debug = "debug";
        if (!this.userValidator.isUserInRole(authToken, ROLE_ADMIN)) {
            throw new IllegalAccessException("Only Administrator can see all users!");
        }

        Page<User> page = this.userData.listUsers(pageable);
        return page.getContent();
    }

    @RequestMapping(value = REGISTER_URL, method = RequestMethod.POST)
    public Map<String, String> registration(@RequestHeader(value = "Authorization") String authToken,
                               @RequestBody RegisterObject payload,
                               BindingResult bindingResult) {

        Map<String, String> responseObj = new HashMap<>();

        User user = new User();
        Set<Role> roles = new HashSet<>();

        if (payload.getRoleId() == 1) {
            if (authToken == null || authToken.length() == 0) {
                throw new UnsupportedOperationException("No Authorization token provided!");
            }

            if(!this.userValidator.isUserInRole(authToken, ROLE_ADMIN)) {
                throw new UnsupportedOperationException("Only Administrator can register another Administrator!");
            }
        }

        roles.add(this.roleRepository.findOne(payload.getRoleId()));
        user.setUsername(payload.getUsername());
        user.setPassword(payload.getPassword());
        user.setPasswordConfirm(payload.getPasswordConfirm());
        user.setRoles(roles);

        this.userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            throw new UnsupportedOperationException(error != null ? error : "Registration failed");
        }

        try {
            createEntity(this.roleRepository.findOne(payload.getRoleId()), user);
            this.userData.save(user);
        } catch (ConstraintViolationException cex) {
            System.out.println(cex.getMessage());
        }

        responseObj.put("message", "Registration successful!");
        return responseObj;
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
