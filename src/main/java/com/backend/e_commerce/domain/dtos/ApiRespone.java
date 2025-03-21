package com.backend.e_commerce.domain.dtos;

import java.util.List;

import com.backend.e_commerce.domain.ApiStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiRespone<T> {
    private ApiStatus status; // "success" hoặc "error"
    private String message;
    private Metadata metadata;
    private List<T> data; // Có thể là List<T> hoặc một object

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Metadata {
        private Integer count; // Số lượng item
    }
}
