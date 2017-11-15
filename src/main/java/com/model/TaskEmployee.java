package com.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.Set;

/**
 * Created by Ivan Minchev on 11/8/2017.
 */

/**
 * Class name is TaskEmployee to avoid ambiguity
 * related with another classes named Task e.g. org.springframework.scheduling.config.Task.
 */
@Entity
@Table(name = "tasks")
public class TaskEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tid;

    @Column
    private String title;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @ManyToMany(mappedBy = "employeeTasks")
    private Set<Employee> assignees;

    @OneToMany(mappedBy = "task")
    private Set<UpdateTaskProgress> updates;


    public long getTid() {
        return this.tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Employer getEmployer() {
        return this.employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public Set<Employee> getAssignees() {
        return this.assignees;
    }

    public void setAssignees(Set<Employee> assignees) {
        this.assignees = assignees;
    }

    public Set<UpdateTaskProgress> getUpdates() {
        return this.updates;
    }

    public void setUpdates(Set<UpdateTaskProgress> updates) {
        this.updates = updates;
    }
}
