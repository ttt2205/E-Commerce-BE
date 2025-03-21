package com.backend.e_commerce.services;

import org.springframework.stereotype.Service;

public interface UserService {
    Boolean createNewUser(String email, String password);

    Boolean checkEmail(String email);
}
