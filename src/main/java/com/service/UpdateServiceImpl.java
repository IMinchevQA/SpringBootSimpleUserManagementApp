package com.service;

import com.model.UpdateTaskProgress;
import com.repository.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Ivan Minchev on 11/10/2017.
 */
@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    UpdateRepository updateRepository;

    @Override
    public void saveUpdate(UpdateTaskProgress update) {
        this.updateRepository.save(update);
    }

    @Override
    public void deleteUpdate(long id) {
        this.updateRepository.delete(id);
    }
}
