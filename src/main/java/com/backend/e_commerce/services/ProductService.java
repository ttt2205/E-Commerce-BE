package com.backend.e_commerce.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.e_commerce.domain.dtos.ProductRequest;
import com.backend.e_commerce.domain.entities.ProductEntity;

public interface ProductService {

    public List<ProductEntity> findAll();

    public Page<ProductEntity> findAll(Pageable pageable);

    public ProductEntity findProductByProductCode(String productCode);

    public boolean createNewProductItem(ProductRequest productRequest);

    public ProductEntity findProductById(Long id);

    public ProductEntity updateParticalProduct(ProductEntity productEntity, ProductRequest productRequest);

    public ProductEntity updateProduct(ProductEntity productEntity, ProductRequest productRequest);

    public ProductEntity deleteProduct(Long id);
}
