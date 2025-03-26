package com.backend.e_commerce.services;

import java.util.Optional;

import com.backend.e_commerce.domain.entities.RoleEntity;

public interface RoleService {
    public Optional<RoleEntity> checkRoleIsExist(String role);
}
