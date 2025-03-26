package com.backend.e_commerce.domain.dtos;

import java.util.List;
import java.util.Map;

import com.backend.e_commerce.domain.ApiStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private ApiStatus status; // "success" hoặc "error"
    private int code;
    private String message;
    private Map<String, Object> metadata;
    private List<T> data; // Có thể là List<T> hoặc một object
}
