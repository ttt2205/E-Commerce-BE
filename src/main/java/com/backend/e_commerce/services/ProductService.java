package com.backend.e_commerce.services;

import java.util.List;

import com.backend.e_commerce.domain.dtos.ProductRequest;
import com.backend.e_commerce.domain.entities.ProductEntity;

public interface ProductService {

    public List<ProductEntity> findAll();

    public ProductEntity findProductByProductCode(String productCode);

    public boolean createNewProductItem(ProductRequest productRequest);

    public ProductEntity findProductByProductId(Long id);

    public ProductEntity updateParticalProduct(ProductEntity productEntity, ProductRequest productRequest);
}
