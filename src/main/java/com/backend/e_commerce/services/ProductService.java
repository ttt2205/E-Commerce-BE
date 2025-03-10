package com.backend.e_commerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.entities.ProductEntity;

@Service
public interface ProductService {

    public List<ProductEntity> findAll();
}
