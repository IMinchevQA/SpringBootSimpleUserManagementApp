package com.javainuse.model;

import com.javainuse.model.Role;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long uid;

    @Column
    private String username;

    @Column
    private String password;

    @Transient
    private String passwordConfirm;

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "uid"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",  referencedColumnName = "rid"))
    private Set<Role> roles;



    public long getUid() {
        return this.uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }


    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


//    @OneToOne(mappedBy = "employer")
//    @Nullable
//    private Employer employer;
//
//    @OneToOne(mappedBy = "employee")
//    @Nullable
//    private Employee employee;
}
