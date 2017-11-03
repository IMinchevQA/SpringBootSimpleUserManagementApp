package com.javainuse.controller;

import com.javainuse.model.Role;
import com.javainuse.repository.RoleRepository;
import com.javainuse.service.*;
import com.javainuse.validator.UserValidator;
import com.javainuse.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static com.javainuse.security.SecurityConstants.REGISTER_URL;

/**
 * Created by Ivan Minchev on 10/24/2017.
 */
@RestController
public class UserController {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_EMPLOYER = "ROLE_EMPLOYER";
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";

    @Autowired
    private UserService userRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployerServiceImpl employerData;

    @Autowired
    private EmployeeServiceImpl employeeData;


    @RequestMapping(value = "/users/changeStatus/{username}", method = RequestMethod.POST)
    public String changeUserStatus(@PathVariable("username") String username) {
        User user = this.userRepository.findByUsername(username);
        String newStatus = "";
        if (user == null) {
            throw new UsernameNotFoundException("No employer found with username: " + username);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username1 = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        System.out.println(username1);

//        if (user.getStatus().equals("active")) {
//            user.setStatus("inactive");
//            newStatus = "Inactive";
//        } else {
//            user.setStatus("active");
//            newStatus = "Active";
//        }
        this.userRepository.save(user);
        return "Status of user with username "
                + user.getUsername()
                + " changed to "
                + newStatus + "!";
    }


    @RequestMapping(value = REGISTER_URL, method = RequestMethod.POST)
    public String registration(@RequestBody RegisterObject payload, BindingResult bindingResult) {

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
            return error != null ? error : "Registration failed";
        }

        this.userRepository.save(user);
        createEntity(this.roleRepository.findOne(payload.getRoleId()), user);

        return "Registration successful.";
    }

    private void createEntity(Role role, User user) {
        try {
            switch (role.getName()) {
                case ROLE_ADMIN:
                    System.out.println("admin");
                    break;
                case ROLE_EMPLOYER:
                    EmployerController employerController = EmployerController.class.newInstance();
                    Method[] methodsEmployerController = EmployerController.class.getDeclaredMethods();

                    Method setEmployerDataMethod = Stream.of(methodsEmployerController)
                            .filter(method -> method.getName().equals("setEmployerData"))
                            .findFirst()
                            .get();

                    Method addEmployerMethod = Stream.of(methodsEmployerController)
                            .filter(method -> method.getName().equals("addNewEmployer"))
                            .findFirst()
                            .get();

                    setEmployerDataMethod.setAccessible(true);
                    setEmployerDataMethod.invoke(employerController, this.employerData);
                    addEmployerMethod.setAccessible(true);
                    addEmployerMethod.invoke(employerController, user);

                    break;
                case ROLE_EMPLOYEE:
                    EmployeeController employeeController = EmployeeController.class.newInstance();
                    Method[] methodsEmployeeController = EmployeeController.class.getDeclaredMethods();

                    Method setEmployeeDataMethod = Stream.of(methodsEmployeeController)
                            .filter(method -> method.getName().equals("setEmployeeData"))
                            .findFirst()
                            .get();

                    Method addEmployeeMethod = Stream.of(methodsEmployeeController)
                            .filter(method -> method.getName().equals("addNewEmployee"))
                            .findFirst()
                            .get();

                    setEmployeeDataMethod.setAccessible(true);
                    setEmployeeDataMethod.invoke(employeeController, this.employeeData);
                    addEmployeeMethod.setAccessible(true);
                    addEmployeeMethod.invoke(employeeController, user);
                    break;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}

class LoginObject {
    private String username;
    private String password;


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
