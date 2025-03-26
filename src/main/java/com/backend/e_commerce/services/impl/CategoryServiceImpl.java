package com.backend.e_commerce.services.impl;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.dtos.CategoryDto;
import com.backend.e_commerce.domain.entities.CategoryEntity;
import com.backend.e_commerce.mappers.CategoryMapper;
import com.backend.e_commerce.repositories.CategoryRepository;
import com.backend.e_commerce.services.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryEntity> findAll() {
        return StreamSupport
                .stream(categoryRepository.findAll().spliterator(), false)
                .toList();
    }

    @Override
    public Page<CategoryEntity> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public CategoryEntity findCategoryById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public boolean createNewCategoryItem(CategoryDto categoryDto) {
        CategoryEntity categoryEntity = categoryMapper.toEntity(categoryDto);
        CategoryEntity categoryIsExist = categoryRepository
                .findCategoryByCategoryName(categoryEntity.getCategoryName());
        if (categoryIsExist != null) {
            return false;
        }
        categoryRepository.save(categoryEntity);
        return true;

    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity categoryEntity, CategoryDto categoryDto) {
        categoryEntity.setCategoryName(categoryDto.getCategoryName());
        categoryEntity.setParentId(categoryDto.getParentId());
        categoryRepository.save(categoryEntity);
        return categoryEntity;
    }

    @Override
    public CategoryEntity deleteCategory(int id) {
        try {
            CategoryEntity deletedCategory = categoryRepository.findById(
                    id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            categoryRepository.delete(deletedCategory);
            return deletedCategory;
        } catch (RuntimeException e) {
            System.out.println("Lỗi deleteCategory: " + e.getMessage());
            return null;
        }
    }

    @Override
    public CategoryEntity findCategoryByCategoryName(String name) {
        return categoryRepository.findCategoryByCategoryName(name);
    }

}
