package com.javainuse.repository;

import com.javainuse.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ivan Minchev on 10/24/2017.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

}
