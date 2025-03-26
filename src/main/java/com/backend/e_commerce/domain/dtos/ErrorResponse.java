package com.backend.e_commerce.domain.dtos;

import com.backend.e_commerce.domain.ApiStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private ApiStatus status; // "success" hoặc "error"
    private int code;
    private String message;
}
