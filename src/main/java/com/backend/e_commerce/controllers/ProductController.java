package com.backend.e_commerce.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.backend.e_commerce.domain.entities.ProductEntity;
import com.backend.e_commerce.services.ProductService;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<ProductEntity> getMethodName() {
        List<ProductEntity> results = productService.findAll();
        return results;
    }

}
