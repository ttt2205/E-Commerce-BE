package com.backend.e_commerce.services;

import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.dtos.SignInRequest;

public interface UserProfileService {
    Boolean createNewUserProfile(String email, SignInRequest signInRequest);
}
