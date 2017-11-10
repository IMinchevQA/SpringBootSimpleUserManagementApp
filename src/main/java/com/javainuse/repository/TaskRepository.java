package com.javainuse.repository;

import com.javainuse.model.TaskEmployee;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Ivan Minchev on 11/9/2017.
 */
public interface TaskRepository extends PagingAndSortingRepository <TaskEmployee, Long> {
}
