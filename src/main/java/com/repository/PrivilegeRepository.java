package com.repository;

import com.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ivan Minchev on 10/26/2017.
 */
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findPrivilegeByName(String name);
}
