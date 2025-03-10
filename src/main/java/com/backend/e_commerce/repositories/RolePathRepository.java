package com.backend.e_commerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.e_commerce.domain.HttpMethod;
import com.backend.e_commerce.domain.entities.PathEntity;
import com.backend.e_commerce.domain.entities.RoleEntity;
import com.backend.e_commerce.domain.entities.RolePathEntity;

@Repository
public interface RolePathRepository extends JpaRepository<RolePathEntity, Integer> {
    boolean existsByRoleAndPathAndMethod(RoleEntity role, PathEntity path, HttpMethod method);
}
