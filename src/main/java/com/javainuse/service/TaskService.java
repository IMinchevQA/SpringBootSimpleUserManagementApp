package com.javainuse.service;

import com.javainuse.model.TaskEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by Ivan Minchev on 11/9/2017.
 */
@Service
public interface TaskService {

    Page<TaskEmployee> listTasks(Pageable pageable);

    TaskEmployee findTaskById(long id);

    void addTask(TaskEmployee task);

    void updateTask(TaskEmployee task, String title);

    void deleteTask(long id);
}
