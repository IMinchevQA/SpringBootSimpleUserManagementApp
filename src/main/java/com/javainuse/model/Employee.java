package com.javainuse.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Ivan Minchev on 10/18/2017.
 */
@Entity
@Table(name = "employees")
public class Employee {
    private static final String subscriberType = "employee";

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column
    private String username;

    @Column(length = 15)
    private String firstName;

    @Column(length = 2)
    private String middleInitial;

    @Column(length = 15)
    private String lastName;

    @Column
    private String employeeNumber;

    @Column(length = 10, unique = true)
    private String departmentID;

    @Column(length = 15)
    private String phoneNumber;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dateOfHire;

//    @Column(nullable = false, length = 20)
    private String job;

//    @Column(nullable = false)
    private Integer formalEducationYears;

//    @Column(nullable = false)
    private String sex;


    private Date dateOfBirth;
    private Integer yearSalary;
    private Integer yearBonus;
    private Integer commission;

    @ManyToOne
    @JoinColumn(name = "employer_id")
//    @JoinTable(
//            name = "employee_employer",
//            joinColumns = @JoinColumn(
//                    name = "employer_id"),
//            inverseJoinColumns = @JoinColumn(
//                    name = "employee_id"))
    private Employer employer;

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
        this.middleInitial = middleInitial.substring(0,1).toUpperCase();
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String familyName) {
        this.lastName = familyName;
    }

    public String getEmployeeNumber() {
        return this.employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getDepartmentID() {
        return this.departmentID;
    }

    public void setDepartmentID(String departmentID) {
        this.departmentID = departmentID;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public Date getDateOfHire() {
        return this.dateOfHire;
    }


    public void setDateOfHire(Date dateOfHire) {
        this.dateOfHire = new java.sql.Date(dateOfHire.getTime());
    }

    public String getJob() {
        return this.job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Integer getFormalEducationYears() {
        return this.formalEducationYears;
    }

    public void setFormalEducationYears(Integer formalEducationYears) {
        this.formalEducationYears = formalEducationYears;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getYearSalary() {
        return this.yearSalary;
    }

    public void setYearSalary(Integer yearSalary) {
        this.yearSalary = yearSalary;
    }

    public Integer getYearBonus() {
        return this.yearBonus;
    }

    public void setYearBonus(Integer yearBonus) {
        this.yearBonus = yearBonus;
    }

    public Integer getComission() {
        return this.commission;
    }

    public void setComission(Integer  comission) {
        this.commission = comission;
    }

    public Employer getEmployer() {
        return this.employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Employee [id=").append(this.id);
        sb.append(", firstName=").append(this.firstName);
        sb.append(", middleInitial=").append(this.middleInitial).append(".");
        sb.append(", lastName=").append(this.lastName);
        sb.append("]");
//        return "Employee [id=" + id + ", firstName=" + this.firstName + ", lastName=" + this.lastName+ "]";
        return sb.toString();
    }
}
