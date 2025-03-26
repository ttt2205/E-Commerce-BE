package com.backend.e_commerce.configs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.backend.e_commerce.domain.HttpMethod;
import com.backend.e_commerce.domain.UserStatus;
import com.backend.e_commerce.domain.entities.PathEntity;
import com.backend.e_commerce.domain.entities.RoleEntity;
import com.backend.e_commerce.domain.entities.RolePathEntity;
import com.backend.e_commerce.domain.entities.UserEntity;
import com.backend.e_commerce.repositories.PathRepository;
import com.backend.e_commerce.repositories.RolePathRepository;
import com.backend.e_commerce.repositories.RoleRepository;
import com.backend.e_commerce.repositories.UserRepository;

@Component
public class Initializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PathRepository pathRepository;

    @Autowired
    private RolePathRepository rolePathRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "thanhtrung22052004@gmail.com";
        String adminPassword = "trung2205";
        // Tạo tài khoản Admin mặc định
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            // Kiểm tra xem role có tồn tại chưa
            RoleEntity roleEntity = roleRepository.findByRoleName("ADMIN")
                    .orElseGet(() -> {
                        RoleEntity newRole = RoleEntity.builder()
                                .roleName("ADMIN")
                                .description("Tài khoản admin")
                                .build();
                        return roleRepository.save(newRole);
                    });

            UserEntity userEntity = UserEntity.builder()
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .status(UserStatus.ACTIVE)
                    .userRoles(Set.of(roleEntity)) // ✅ Gán role vào user
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userRepository.save(userEntity);

            System.out.println("Admin duoc tao thanh cong");
        } else {
            System.out.println("Admin da ton tai");
        }

        // Cấp quyền truy cập cho tài khoản Admin và User
        RoleEntity adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseGet(() -> {
                    RoleEntity newRole = RoleEntity.builder()
                            .roleName("ADMIN")
                            .description("Tài khoản admin")
                            .build();
                    return roleRepository.save(newRole);
                });

        RoleEntity userRole = roleRepository.findByRoleName("USER")
                .orElseGet(() -> {
                    RoleEntity newRole = RoleEntity.builder()
                            .roleName("USER")
                            .description("Tài khoản người dùng")
                            .build();
                    return roleRepository.save(newRole);
                });

        // Danh sách các API
        List<String> paths = List.of(
                "/api/v1/dashboard/",
                "/api/v1/products/",
                "/api/v1/orders/",
                "/api/v1/order-details/",
                "/api/v1/roles/",
                "/api/v1/shipping/",
                "/api/v1/users/",
                "/api/v1/paths/",
                "/api/v1/categories/",
                "/api/v1/promotions/",
                "/api/v1/suppliers/");

        List<PathEntity> listPaths = paths.stream()
                .map(url -> pathRepository.findByUrl(url).orElseGet(() -> {
                    PathEntity path = PathEntity.builder()
                            .url(url)
                            .build();
                    return pathRepository.save(path);
                }))
                .collect(Collectors.toList());

        // Cấp quyền ADMIN có thể truy cập tất cả phương thức trên mọi API
        for (PathEntity pathEntity : listPaths) {
            for (HttpMethod httpMethod : HttpMethod.values()) {
                if (!rolePathRepository.existsByRoleAndPathAndMethod(adminRole, pathEntity, httpMethod)) {
                    RolePathEntity rolePathEntity = RolePathEntity.builder()
                            .role(adminRole)
                            .path(pathEntity)
                            .method(httpMethod)
                            .build();
                    rolePathRepository.save(rolePathEntity);
                }
            }
        }
    }

}
