package com;

import com.model.Privilege;
import com.model.Role;
import com.model.User;
import com.repository.PrivilegeRepository;
import com.repository.RoleRepository;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Ivan Minchev on 10/26/2017.
 */
@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_EMPLOYER = "ROLE_EMPLOYER";
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String READ_PRIVILEGE = "READ_PRIVILEGE";
    private static final String WRITE_PRIVILEGE = "WRITE_PRIVILEGE";
    private static final String DELETE_PRIVILEGE = "DELETE_PRIVILEGE";

    /** The value defines whether initial data will be loaded in userRepository(Admin user), roleRepository and privilegeRepository!
     * true -  no data will be loaded in the a.m. repositories
     * false - initial data will be loaded in a.m. repositories
     */
    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        if (this.alreadySetup) {
            return;
        }

        Privilege readPrivilege = createPrivilegeIfNotFound(READ_PRIVILEGE);
        Privilege writePrivilege = createPrivilegeIfNotFound(WRITE_PRIVILEGE);
        Privilege deletePrivilege = createPrivilegeIfNotFound(DELETE_PRIVILEGE);

        Set<Privilege> adminPrivileges = new HashSet(Arrays.asList(readPrivilege, writePrivilege, deletePrivilege));
        Set<Privilege> employerPrivileges = new HashSet(Arrays.asList(readPrivilege, writePrivilege));
        Set<Privilege> employeePrivileges = new HashSet(Arrays.asList(readPrivilege));

        List<Role> roles = new ArrayList<>();

        createRoleIfNotFound(ROLE_ADMIN, adminPrivileges);
        createRoleIfNotFound(ROLE_EMPLOYER, employerPrivileges);
        createRoleIfNotFound(ROLE_EMPLOYEE, employeePrivileges);
        this.roleRepository.findAll().forEach(roles::add);

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(this.roleRepository.findRoleByName(ROLE_ADMIN));
        User adminUser = new User();
        adminUser.setUsername("Admin");
        adminUser.setPassword(bCryptPasswordEncoder.encode("Administrator"));
        adminUser.setPasswordConfirm("Administrator");
        adminUser.setRoles(adminRoles);

        this.userRepository.save(adminUser);

        alreadySetup = true;
    }



    @Transactional
    private Role createRoleIfNotFound(String roleName, Set<Privilege> privileges) {

        Role role = roleRepository.findRoleByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }


    @Transactional
    private Privilege createPrivilegeIfNotFound(String privilegeName) {

        Privilege privilege = privilegeRepository.findPrivilegeByName(privilegeName);
        if (privilege == null) {
            privilege = new Privilege();
            privilege.setName(privilegeName);
            this.privilegeRepository.save(privilege);
        }
        return privilege;
    }
}
