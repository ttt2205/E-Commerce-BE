package com.backend.e_commerce.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.e_commerce.domain.dtos.CategoryDto;
import com.backend.e_commerce.domain.dtos.ProductRequest;
import com.backend.e_commerce.domain.entities.CategoryEntity;
import com.backend.e_commerce.domain.entities.ProductEntity;

public interface CategoryService {
    public List<CategoryEntity> findAll();

    public Page<CategoryEntity> findAll(Pageable pageable);

    public CategoryEntity findCategoryById(int id);

    public boolean createNewCategoryItem(CategoryDto categoryDto);

    // public CategoryEntity updateParticalCategory(CategoryEntity categoryEntity,
    // CategoryDto categoryDto);

    public CategoryEntity updateCategory(CategoryEntity categoryEntity, CategoryDto categoryDto);

    public CategoryEntity deleteCategory(int id);

    public CategoryEntity findCategoryByCategoryName(String name);
}
