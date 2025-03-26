package com.backend.e_commerce.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.e_commerce.repositories.RoleRepository;
import com.backend.e_commerce.services.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceIml implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Optional<com.backend.e_commerce.domain.entities.RoleEntity> checkRoleIsExist(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

}
