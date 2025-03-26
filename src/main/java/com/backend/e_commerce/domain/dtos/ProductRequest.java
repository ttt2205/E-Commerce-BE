package com.backend.e_commerce.domain.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.backend.e_commerce.domain.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    private String productName;

    private String description;

    private String productCode;

    private BigDecimal price;

    private String status = "ACTIVE";
}
