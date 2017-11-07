package com.javainuse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Ivan Minchev on 10/18/2017.
 */
@Entity
@Table(name = "employees")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Employee {
    private static final String subscriberType = "employee";

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

    @Column
    private String employeeNumber;

    @Column
    private String departmentID;

    @Column
    private String phoneNumber;

    @Column
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dateOfHire;

    @Column
    private String job;

    @Column
    private Integer formalEducationYears;

    @Column
    private String sex;

    @Column
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @Column
    private BigDecimal yearSalary;

    @Column
    private BigDecimal yearBonus;

    @Column
    private BigDecimal commission;

    @JsonIgnore
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
        if (middleInitial != null) {
            this.middleInitial = middleInitial.toUpperCase();
        }
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
        if (dateOfHire != null) {
            this.dateOfHire = new java.sql.Date(dateOfHire.getTime());
        }
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
        if (dateOfBirth != null) {
            this.dateOfBirth = new java.sql.Date(dateOfBirth.getTime());
        }
    }

    public BigDecimal getYearSalary() {
        return this.yearSalary;
    }

    public void setYearSalary(BigDecimal yearSalary) {
        this.yearSalary = yearSalary;
    }

    public BigDecimal getYearBonus() {
        return this.yearBonus;
    }

    public void setYearBonus(BigDecimal yearBonus) {
        this.yearBonus = yearBonus;
    }

    public BigDecimal getCommission() {
        return this.commission;
    }

    public void setCommission(BigDecimal  commission) {
        this.commission = commission;
    }



    public Employer getEmployer() {
        return this.employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Employee [id=").append(this.id);
//        sb.append(", firstName=").append(this.firstName);
//        sb.append(", middleInitial=").append(this.middleInitial).append(".");
//        sb.append(", lastName=").append(this.lastName);
//        sb.append("]");
////        return "Employee [id=" + id + ", firstName=" + this.firstName + ", lastName=" + this.lastName+ "]";
//        return sb.toString();
//    }
}