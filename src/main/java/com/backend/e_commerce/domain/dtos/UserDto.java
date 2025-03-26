package com.backend.e_commerce.domain.dtos;

import java.util.List;

public class UserDto {
    private Long userId;
    private String email;
    private List<String> roles;
    private List<Long> orderIds;
}
