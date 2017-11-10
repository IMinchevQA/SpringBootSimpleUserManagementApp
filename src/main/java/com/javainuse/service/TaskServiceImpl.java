package com.javainuse.service;

import com.javainuse.model.TaskEmployee;
import com.javainuse.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by Ivan Minchev on 11/9/2017.
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Override
    public Page<TaskEmployee> listTasks(Pageable pageable) {

        return this.taskRepository.findAll(pageable);
    }

    @Override
    public TaskEmployee findTaskById(long id) {
        return this.taskRepository.findOne(id);
    }

    @Override
    public void addTask(TaskEmployee task) {
        this.taskRepository.save(task);
    }

    @Override
    public void updateTask(TaskEmployee task, String title) {
        this.taskRepository.save(task);
    }

    @Override
    public void deleteTask(long id) {
        this.taskRepository.delete(id);
    }
}
