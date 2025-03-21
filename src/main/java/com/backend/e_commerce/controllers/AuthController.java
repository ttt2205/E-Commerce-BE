package com.backend.e_commerce.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.e_commerce.domain.ApiStatus;
import com.backend.e_commerce.domain.dtos.ApiRespone;
import com.backend.e_commerce.domain.dtos.AuthResponse;
import com.backend.e_commerce.domain.dtos.LoginRequest;
import com.backend.e_commerce.domain.dtos.SignInRequest;
import com.backend.e_commerce.services.AuthenticationService;
import com.backend.e_commerce.services.UserProfileService;
import com.backend.e_commerce.services.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthenticationService authenticationService;

        private final UserService userService;

        private final UserProfileService userProfileService;

        @PostMapping("/login")
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
        @PostMapping("/sign-in")
        public ResponseEntity<ApiRespone> postCreateNewUser(@RequestBody SignInRequest signInRequest) {
                String email = signInRequest.getEmail();
                String password = signInRequest.getPassword();

                if (userService.checkEmail(email)) {
                        ApiRespone apiRespone = ApiRespone.builder()
                                        .status(ApiStatus.IS_EXIST)
                                        .message("Email đã tồn tại. Vui lòng sử dụng email khác.")
                                        .data(null)
                                        .build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(apiRespone);
                }

                if (userService.createNewUser(email, password)) {
                        userProfileService.createNewUserProfile(email, signInRequest);
                        ApiRespone apiRespone = ApiRespone.builder()
                                        .status(ApiStatus.SUCCESS)
                                        .message("Đăng ký thành công!")
                                        .data(null)
                                        .build();
                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(apiRespone);
                } else {
                        ApiRespone apiRespone = ApiRespone.builder()
                                        .status(ApiStatus.FAIL)
                                        .message("Có lỗi xảy ra vui lòng kiểm tra thông tin các trường!")
                                        .data(null)
                                        .build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(apiRespone);
                }
        }
}
