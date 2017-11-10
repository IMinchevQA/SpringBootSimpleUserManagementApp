package com.javainuse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Ivan Minchev on 11/8/2017.
 */

/**
 * Class name is UpdateTaskProgress to avoid ambiguity
 * related with another classes named UpdateTaskProgress e.g. org.hibernate.sql.Update.
 */

@Entity
@Table(name = "Updates")
public class UpdateTaskProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Work description message;
     */
    @Column
    private String message;

    @Column
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date updateTime;

    @JsonIgnore
    @ManyToOne
    private TaskEmployee task;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime() {
        this.updateTime = new java.sql.Date(new Date().getTime());
    }

    public TaskEmployee getTask() {
        return this.task;
    }

    public void setTask(TaskEmployee task) {
        this.task = task;
    }
}
