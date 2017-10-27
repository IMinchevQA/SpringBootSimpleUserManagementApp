package com.javainuse.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long rid;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @ManyToMany
    @JoinTable(
            name = "roles_privileges",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "rid"),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id", referencedColumnName = "pid"))
    private Set<Privilege> privileges;


    public long getId() {
        return this.rid;
    }

    public void setId(long rid) {
        this.rid = rid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Privilege> getPrivileges() {
        return this.privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }
}