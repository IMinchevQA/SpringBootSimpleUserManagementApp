package com.javainuse.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ivan Minchev on 10/19/2017.
 */
@Entity
@Table(name = "employers")
public class Employer {
    private static final  String subscriberType = "employer";

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(nullable = false, length = 15)
    private String firstName;
    @Column(nullable = false, length = 2)
    private Character middleInitial;
    @Column(nullable = false, length = 15)
    private String lastName;

//    @OneToOne(mappedBy = "User")
//    private User user;
//
//    public User getUser() {
//        return this.user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

    @OneToMany(mappedBy = "employer")
    private Set<Employee> employees = new HashSet<Employee>();

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubscriberType() {
        return subscriberType;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Character getMiddleInitial() {
        return this.middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = Character.toUpperCase(middleInitial.charAt(0));
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


//    public Set<Employee> getEmployees() {
//        return this.employees;
//    }
//
//    public void setEmployees(Set<Employee> employees) {
//        this.employees = employees;
//    }
}
