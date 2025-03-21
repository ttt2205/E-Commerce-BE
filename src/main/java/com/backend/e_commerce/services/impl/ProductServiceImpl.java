package com.backend.e_commerce.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import com.backend.e_commerce.controllers.ProductController;
import com.backend.e_commerce.domain.dtos.ProductRequest;
import com.backend.e_commerce.domain.entities.ProductEntity;
import com.backend.e_commerce.repositories.ProductRepository;
import com.backend.e_commerce.services.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductEntity> findAll() {
        return StreamSupport.stream(productRepository
                .findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public boolean createNewProductItem(ProductRequest productRequest) {
        ProductEntity productIsExist = productRepository.findProductByProductCode(productRequest.getProductCode());
        if (productIsExist != null)
            return false;
        ProductEntity productEntity = ProductEntity.builder()
                .productName(productRequest.getProductName())
                .description(productRequest.getDescription())
                .productCode(productRequest.getProductCode())
                .build();
        ProductEntity productSaved = productRepository.save(productEntity);
        return productSaved.getProductId() != null;
    }

    @Override
    public ProductEntity findProductByProductCode(String productCode) {
        return productRepository.findProductByProductCode(productCode);
    }

    @Override
    public ProductEntity findProductByProductId(Long id) {
        return productRepository.findProductByProductId(id);
    }

    @Override
    public ProductEntity updateParticalProduct(ProductEntity productEntity, ProductRequest productRequest) {
        if (productRequest.getProductName() != null)
            productEntity.setProductName(productRequest.getProductName());
        if (productRequest.getDescription() != null)
            productEntity.setDescription(productRequest.getDescription());
        if (productRequest.getProductCode() != null)
            productEntity.setProductCode(productRequest.getProductCode());
        productEntity.setUpdatedAt(LocalDateTime.now());
        productRepository.save(productEntity);
        return productEntity;
    }

}
