package com.backend.e_commerce.services.impl;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.entities.UserEntity;
import com.backend.e_commerce.repositories.UserRepository;
import com.backend.e_commerce.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Boolean createNewUser(String email, String password) {
        try {
            String passwordHash = encodePassword(password);

            UserEntity userEntity = UserEntity.builder()
                    .email(email)
                    .passwordHash(passwordHash)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userRepository.save(userEntity);

            return true;
        } catch (Exception e) {
            System.out.println("Create New User is fail!");
            System.out.println("Error: " + e);
            return false;
        }
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public Boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
