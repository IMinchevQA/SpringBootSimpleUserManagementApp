package com.repository;

import com.model.TaskEmployee;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Ivan Minchev on 11/9/2017.
 */
public interface TaskRepository extends PagingAndSortingRepository <TaskEmployee, Long> {
}
