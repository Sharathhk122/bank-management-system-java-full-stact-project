package com.bmsp.bmsp.repository.auth;

import com.bmsp.bmsp.model.auth.ERole;
import com.bmsp.bmsp.model.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}