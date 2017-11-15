package com.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ivan Minchev on 10/19/2017.
 */
@Entity
@Table(name = "employers")
public class Employer {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column
    private String username;

    @Column
    private String firstName;

    @Column
    private String middleInitial;

    @Column
    private String lastName;


    @OneToMany(mappedBy = "employer")
    private Set<Employee> employees = new HashSet<Employee>();

    @OneToMany(mappedBy = "employer")
    private Set<TaskEmployee> taskEmployees = new HashSet<>();

    @Column
    private boolean isActive = false;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return this.middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive() {
        this.isActive = !isActive;
    }

    public Set<TaskEmployee> getTaskEmployees() {
        return this.taskEmployees;
    }

    public void setTaskEmployees(Set<TaskEmployee> taskEmployees) {
        this.taskEmployees = taskEmployees;
    }
}
