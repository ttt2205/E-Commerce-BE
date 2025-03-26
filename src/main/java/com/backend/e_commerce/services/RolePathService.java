package com.backend.e_commerce.services;

import com.backend.e_commerce.domain.HttpMethod;
import com.backend.e_commerce.domain.entities.PathEntity;
import com.backend.e_commerce.domain.entities.RoleEntity;

public interface RolePathService {
    public boolean existsByRoleAndPathAndMethod(
            RoleEntity role,
            PathEntity path,
            HttpMethod method);
}
