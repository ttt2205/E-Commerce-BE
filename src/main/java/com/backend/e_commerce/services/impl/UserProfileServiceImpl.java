package com.backend.e_commerce.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.Gender;
import com.backend.e_commerce.domain.dtos.SignInRequest;
import com.backend.e_commerce.domain.entities.UserEntity;
import com.backend.e_commerce.domain.entities.UserProfileEntity;
import com.backend.e_commerce.repositories.UserProfileRepository;
import com.backend.e_commerce.repositories.UserRepository;
import com.backend.e_commerce.services.UserProfileService;
import com.backend.e_commerce.utils.Convert;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final UserRepository userRepository;

    @Override
    public Boolean createNewUserProfile(String email, SignInRequest signInRequest) {
        try {
            Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);

            if (optionalUserEntity.isEmpty()) {
                System.out.println("User not found with email: " + email);
                return false;
            }

            UserEntity userEntity = optionalUserEntity.get();

            UserProfileEntity userProfileEntity = UserProfileEntity.builder()
                    .user(userEntity)
                    .firstName(signInRequest.getFirstName())
                    .lastName(signInRequest.getLastName())
                    .phone(signInRequest.getPhone())
                    .address(signInRequest.getAddress())
                    .dateOfBirth(
                            Convert.convertStringToLocalDateTime(signInRequest.getBirthDay()))
                    .avatarURL("")
                    .gender(signInRequest.getGender() != null ? Gender.valueOf(signInRequest.getGender().toUpperCase())
                            : Gender.OTHER)
                    .build();
            userProfileRepository.save(userProfileEntity);

            return true;
        } catch (Exception e) {
            System.out.println("Create New User Profile is fail!");
            System.out.println("Error: " + e);
            return false;
        }
    }

}
