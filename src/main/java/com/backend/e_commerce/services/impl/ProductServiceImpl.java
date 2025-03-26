package com.backend.e_commerce.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.ProductStatus;
import com.backend.e_commerce.domain.dtos.ProductRequest;
import com.backend.e_commerce.domain.entities.ProductEntity;
import com.backend.e_commerce.mappers.ProductMapper;
import com.backend.e_commerce.repositories.ProductRepository;
import com.backend.e_commerce.services.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    public List<ProductEntity> findAll() {
        return StreamSupport.stream(productRepository
                .findAll()
                .spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductEntity> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public boolean createNewProductItem(ProductRequest productRequest) {
        ProductEntity productIsExist = productRepository.findProductByProductCode(productRequest.getProductCode());
        if (productIsExist != null)
            return false;
        ProductEntity productEntity = ProductEntity.builder()
                .productName(productRequest.getProductName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .productCode(productRequest.getProductCode())
                .status(ProductStatus.ACTIVE)
                .build();
        ProductEntity productSaved = productRepository.save(productEntity);
        return productSaved.getId() != null;
    }

    @Override
    public ProductEntity findProductByProductCode(String productCode) {
        return productRepository.findProductByProductCode(productCode);
    }

    @Override
    public ProductEntity findProductById(Long id) {
        return productRepository.findProductById(id);
    }

    @Override
    public ProductEntity updateParticalProduct(ProductEntity productEntity, ProductRequest productRequest) {
        if (productRequest.getStatus() != null)
            productEntity.setStatus(ProductStatus.valueOf(productRequest.getStatus().toUpperCase()));
        productEntity.setUpdatedAt(LocalDateTime.now());
        productRepository.save(productEntity);
        return productEntity;
    }

    @Override
    public ProductEntity updateProduct(ProductEntity productEntity, ProductRequest productRequest) {
        productEntity.setProductName(productRequest.getProductName());
        productEntity.setProductCode(productRequest.getProductCode());
        productEntity.setDescription(productRequest.getDescription());
        productEntity.setCreatedAt(productEntity.getCreatedAt());
        productEntity.setUpdatedAt(LocalDateTime.now());
        productEntity.setPrice(productRequest.getPrice());
        productEntity.setStatus(ProductStatus.valueOf(productRequest.getStatus().toUpperCase()));
        productRepository.save(productEntity);
        return productEntity;
    }

    @Override
    public ProductEntity deleteProduct(Long id) {
        try {
            ProductEntity deletedProduct = productRepository.findById(
                    id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            productRepository.delete(deletedProduct);
            return deletedProduct;
        } catch (RuntimeException e) {
            System.out.println("Lỗi deleteProduct: " + e.getMessage());
            return null;
        }
    }

}
