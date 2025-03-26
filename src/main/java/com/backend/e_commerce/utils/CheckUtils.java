package com.backend.e_commerce.utils;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.ProductStatus;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CheckUtils {
    public boolean checkValidProductStatus(String status) {
        Set<String> validStatus = EnumSet.allOf(ProductStatus.class)
                .stream()
                .map(ProductStatus::name)
                .collect(Collectors.toSet());
        return validStatus.contains(status);
    }
}
