package com.backend.e_commerce.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.e_commerce.domain.ApiStatus;
import com.backend.e_commerce.domain.dtos.ApiResponse;
import com.backend.e_commerce.domain.dtos.AuthResponse;
import com.backend.e_commerce.domain.dtos.LoginRequest;
import com.backend.e_commerce.domain.dtos.SignInRequest;
import com.backend.e_commerce.services.AuthenticationService;
import com.backend.e_commerce.services.UserProfileService;
import com.backend.e_commerce.services.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {

        private final AuthenticationService authenticationService;

        private final UserService userService;

        private final UserProfileService userProfileService;

        @PostMapping("login")
        public ResponseEntity<AuthResponse> postLoginUserAccount(@RequestBody LoginRequest loginRequest) {
                UserDetails userDetails = authenticationService.authenticate(loginRequest.getEmail(),
                                loginRequest.getPassword());
                String tokenValue = authenticationService.generateToken(userDetails);
                List<String> roles = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority) // Lấy tên role
                                .collect(Collectors.toList());
                AuthResponse authResponse = AuthResponse.builder()
                                .token(tokenValue)
                                .expiresIn(86400)
                                .roles(roles)
                                .build();
                return ResponseEntity.ok(authResponse);
        }

        @SuppressWarnings("rawtypes")
        @PostMapping("sign-in")
        public ResponseEntity<ApiResponse> postCreateNewUser(@RequestBody SignInRequest signInRequest) {
                String email = signInRequest.getEmail();
                String password = signInRequest.getPassword();

                if (userService.checkEmail(email)) {
                        ApiResponse apiRespone = ApiResponse.builder()
                                        .status(ApiStatus.IS_EXIST)
                                        .code(HttpStatus.CONFLICT.value())
                                        .message("Email đã tồn tại. Vui lòng sử dụng email khác.")
                                        .metadata(Collections.emptyMap())
                                        .data(null)
                                        .build();
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                        .body(apiRespone);
                }

                if (userService.createNewUser(email, password)) {
                        userProfileService.createNewUserProfile(email, signInRequest);
                        ApiResponse apiRespone = ApiResponse.builder()
                                        .status(ApiStatus.SUCCESS)
                                        .message("Đăng ký thành công!")
                                        .data(null)
                                        .metadata(Collections.emptyMap())
                                        .build();
                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(apiRespone);
                } else {
                        ApiResponse apiRespone = ApiResponse.builder()
                                        .status(ApiStatus.BAD_REQUEST)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Có lỗi xảy ra vui lòng kiểm tra thông tin các trường!")
                                        .metadata(Collections.emptyMap())
                                        .data(null)
                                        .build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(apiRespone);
                }
        }
}
