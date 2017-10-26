package com.javainuse.model;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Ivan Minchev on 10/26/2017.
 */
@Entity
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pid;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Set<Role> roles;

    public long getId() {
        return this.pid;
    }

    public void setId(long pid) {
        this.pid = pid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getUsers() {
        return this.roles;
    }

    public void setUsers(Set<Role> roles) {
        this.roles = roles;
    }
}
