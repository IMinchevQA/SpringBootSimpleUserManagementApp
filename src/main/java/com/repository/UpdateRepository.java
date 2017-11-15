package com.repository;

import com.model.UpdateTaskProgress;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Ivan Minchev on 11/10/2017.
 */
public interface UpdateRepository extends PagingAndSortingRepository<UpdateTaskProgress, Long> {
}
