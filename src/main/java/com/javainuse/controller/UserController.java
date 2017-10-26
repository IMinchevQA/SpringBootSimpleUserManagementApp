package com.javainuse.controller;

import com.javainuse.model.Role;
import com.javainuse.repository.RoleRepository;
import com.javainuse.service.EmployeeServiceImpl;
import com.javainuse.validator.UserValidator;
import com.javainuse.model.User;
import com.javainuse.service.SecurityService;
import com.javainuse.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Ivan Minchev on 10/24/2017.
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeServiceImpl employeeData;

    @RequestMapping(value = "/users/register", method = RequestMethod.POST)
    public String registration(@RequestBody RegisterObject payload, BindingResult bindingResult) {

        if (this.roleRepository.findAll().isEmpty()) {
            addRolesInRoleRepository();
        }

        User user = new User();
        Set<Role> roles = new HashSet<>();
        roles.add(this.roleRepository.findOne(payload.getRoleId()));
        user.setUsername(payload.getUsername());
        user.setPassword(payload.getPassword());
        user.setPasswordConfirm(payload.getPasswordConfirm());
        user.setRoles(roles);

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            return error != null ? error : "Registration failed";
        }

        userService.save(user);
        roles.stream().forEach(role -> role.getUsers().add(user));
        createEntity(this.roleRepository.findOne(payload.getRoleId()), user, employeeData);

        securityService.autologin(user.getUsername(), user.getPasswordConfirm());

        return "Registration successful.";
    }

    private void createEntity(Role role, User user, EmployeeServiceImpl employeeData) {
        try {
            switch (role.getName()) {
                case "admin":
                    System.out.println("admin");
                    break;
                case "employer":
                    System.out.println("employer");
                    break;
                case "employee":
                    EmployeeController employeeController = EmployeeController.class.newInstance();
                    Method[] methods = EmployeeController.class.getDeclaredMethods();

                    Method setEmployeeDataMethod = Stream.of(methods)
                            .filter(method -> method.getName().equals("setEmployeeData"))
                            .findFirst()
                            .get();

                    Method addEmployeeMethod = Stream.of(methods)
                            .filter(method -> method.getName().equals("addNewEmployee"))
                            .findFirst()
                            .get();

                    addEmployeeMethod.setAccessible(true);
                    setEmployeeDataMethod.setAccessible(true);
                    setEmployeeDataMethod.invoke(employeeController, employeeData);
                    addEmployeeMethod.invoke(employeeController, user);
                    break;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void addRolesInRoleRepository() {
        Role adminRole = new Role();
        adminRole.setName("admin");
        adminRole.setUsers(new HashSet<User>());
        Role employerRole = new Role();
        employerRole.setName("employer");
        employerRole.setUsers(new HashSet<User>());
        Role employeeRole = new Role();
        employeeRole.setName("employee");
        employeeRole.setUsers(new HashSet<User>());
        this.roleRepository.save(adminRole);
        this.roleRepository.save(employerRole);
        this.roleRepository.save(employeeRole);
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
