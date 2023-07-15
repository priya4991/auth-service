package com.authservice.jwtauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authservice.jwtauth.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
