package com.backend.e_commerce.services.impl;

import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.HttpMethod;
import com.backend.e_commerce.domain.entities.PathEntity;
import com.backend.e_commerce.domain.entities.RoleEntity;
import com.backend.e_commerce.repositories.RolePathRepository;
import com.backend.e_commerce.services.RolePathService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolePathServiceIml implements RolePathService {

    private final RolePathRepository rolePathRepository;

    @Override
    public boolean existsByRoleAndPathAndMethod(RoleEntity role, PathEntity path, HttpMethod method) {
        return rolePathRepository.existsByRoleAndPathAndMethod(
                role,
                path,
                method);
    }

}
