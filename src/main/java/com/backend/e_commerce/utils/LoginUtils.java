package com.backend.e_commerce.utils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.HttpMethod;
import com.backend.e_commerce.domain.entities.PathEntity;
import com.backend.e_commerce.domain.entities.RoleEntity;
import com.backend.e_commerce.domain.entities.UserEntity;
import com.backend.e_commerce.security.ShopUserDetail;
import com.backend.e_commerce.services.PathService;
import com.backend.e_commerce.services.RolePathService;
import com.backend.e_commerce.services.RoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUtils {

    private final PathService pathService;

    private final RoleService roleService;

    private final RolePathService rolePathService;

    public ShopUserDetail getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.error("Khong tim thay authentication trong SecurityContext");
            return null;
        }

        if (!(authentication.getPrincipal() instanceof ShopUserDetail)) {
            log.error("Principal khong phai kieu ShopUserDetail, ma la: " +
                    authentication.getPrincipal().getClass());
            return null;
        }

        return (ShopUserDetail) authentication.getPrincipal();
    }

    public boolean checkRolePass(String path, HttpMethod method) {
        // ✅ Lấy danh sách role của người dùng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Check Path
            Optional<PathEntity> pathOptional = pathService.checkPathIsExist(path);
            if (!pathOptional.isPresent())
                return false;
            PathEntity pathEntity = pathOptional.get();

            // Check Role
            List<RoleEntity> roleList = new ArrayList<>();

            for (String role : roles) {
                // Check Role
                Optional<RoleEntity> roleOptional = roleService.checkRoleIsExist(role);
                if (roleOptional.isPresent()) {
                    RoleEntity roleEntity = roleOptional.get();
                    roleList.add(roleEntity);
                }
            }

            if (roleList.isEmpty())
                return false;

            // Check Role_Path
            for (RoleEntity roleEntity : roleList) {
                if (rolePathService.existsByRoleAndPathAndMethod(roleEntity, pathEntity, method)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
