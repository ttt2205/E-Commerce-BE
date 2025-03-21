package com.backend.e_commerce.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDay;
    private String phone;
    private String gender;
}
