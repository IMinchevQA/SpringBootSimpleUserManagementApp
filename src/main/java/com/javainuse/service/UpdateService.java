package com.javainuse.service;

import com.javainuse.model.UpdateTaskProgress;
import org.springframework.stereotype.Service;

/**
 * Created by Ivan Minchev on 11/10/2017.
 */
@Service
public interface UpdateService {
    void saveUpdate(UpdateTaskProgress update);

    void deleteUpdate(long id);
}
