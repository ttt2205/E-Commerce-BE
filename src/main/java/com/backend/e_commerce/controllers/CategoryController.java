package com.backend.e_commerce.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.e_commerce.domain.ApiStatus;
import com.backend.e_commerce.domain.HttpMethod;
import com.backend.e_commerce.domain.dtos.ApiResponse;
import com.backend.e_commerce.domain.dtos.CategoryDto;
import com.backend.e_commerce.domain.dtos.ErrorResponse;
import com.backend.e_commerce.domain.dtos.CategoryDto;
import com.backend.e_commerce.domain.dtos.CategoryDto;
import com.backend.e_commerce.domain.entities.CategoryEntity;
import com.backend.e_commerce.domain.entities.CategoryEntity;
import com.backend.e_commerce.domain.entities.CategoryEntity;
import com.backend.e_commerce.mappers.CategoryMapper;
import com.backend.e_commerce.security.ShopUserDetail;
import com.backend.e_commerce.services.CategoryService;
import com.backend.e_commerce.utils.CheckUtils;
import com.backend.e_commerce.utils.LoginUtils;
import com.backend.e_commerce.utils.UrlParamUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories/")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryMapper categoryMapper;

    private final LoginUtils loginUltils;

    private final CheckUtils checkUtils;

    private final UrlParamUtils urlParamUtils;

    @GetMapping
    public ResponseEntity<ApiResponse<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam MultiValueMap<String, String> params) {
        List<String> sortParams = params.get("sort"); // Lấy danh sách tham số `sort` co dang:
                                                      // [[id,asc],[name,desc]]
        Sort sorting = urlParamUtils.getSort(sortParams);
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<CategoryEntity> categoryPage = categoryService.findAll(pageable);

        // Lay danh sach da loc
        List<CategoryEntity> results = categoryPage.getContent();
        List<CategoryDto> categoryDtos = results.stream()
                .map(categoryMapper::toDto)
                .toList();
        ApiResponse<CategoryDto> apiRespone = ApiResponse.<CategoryDto>builder()
                .status(ApiStatus.SUCCESS)
                .message("Get category data success!")
                .metadata(Map.of(
                        "totalElements", categoryPage.getTotalElements(),
                        "totalPage", categoryPage.getTotalPages(),
                        "elementOfPage", categoryPage.getNumberOfElements(), // so phan tu cua
                        "currentPage", categoryPage.getNumber()))
                .data(categoryDtos)
                .build();
        return ResponseEntity.ok(apiRespone);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable int id) {
        CategoryEntity categoryEntity = categoryService.findCategoryById(id);
        if (categoryEntity != null) {
            CategoryDto categoryDto = categoryMapper.toDto(categoryEntity);
            ApiResponse<CategoryDto> apiRespone = ApiResponse.<CategoryDto>builder()
                    .status(ApiStatus.SUCCESS)
                    .code(HttpStatus.OK.value())
                    .message("Get category data success!")
                    .metadata(Collections.emptyMap())
                    .data(List.of(
                            categoryDto))
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(apiRespone);
        } else {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.NOT_FOUND)
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("category not found!")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(apiRespone);
        }
    }

    @PostMapping
    public ResponseEntity<?> postCreateNewcategory(@RequestBody CategoryDto categoryDto) {
        // Kiem tra thong tin login cua user
        ShopUserDetail shopUserDetail = loginUltils.getCurrentUser();
        if (shopUserDetail == null) {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.UNAUTHORIZED)
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message("User chưa đăng nhập!")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiRespone);
        }

        // Check role and path
        if (!loginUltils.checkRolePass("/api/v1/categories/", HttpMethod.POST)) {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.FORBIDDEN)
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("User chưa đăng nhập hoặc không có quyền truy cập!")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiRespone);
        }

        // Tao moi category
        if (categoryService.createNewCategoryItem(categoryDto)) {
            CategoryEntity categoryEntity = categoryService
                    .findCategoryByCategoryName(categoryDto.getCategoryName());
            CategoryDto categoryDtoResponse = categoryMapper.toDto(categoryEntity);
            ApiResponse<CategoryDto> apiRespone = ApiResponse.<CategoryDto>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Create category data success!")
                    .code(HttpStatus.CREATED.value())
                    .metadata(Collections.emptyMap())
                    .data(List.of(
                            categoryDtoResponse))
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(apiRespone);
        } else {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.BAD_REQUEST)
                    .message("Failed to create category! Please check your input data or category is exist.")
                    .code(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(apiRespone);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> putCategory(@PathVariable int id, @RequestBody CategoryDto categoryDto) {
        // Kiem tra thong tin login cua user
        ShopUserDetail shopUserDetail = loginUltils.getCurrentUser();
        if (shopUserDetail == null) {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.UNAUTHORIZED)
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message("User chưa đăng nhập!")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiRespone);
        }

        // Check role and path
        if (!loginUltils.checkRolePass("/api/v1/categorys/", HttpMethod.PUT)) {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.FORBIDDEN)
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("User chưa đăng nhập hoặc không có quyền truy cập!")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiRespone);
        }

        // Check category
        CategoryEntity categoryEntity = categoryService.findCategoryById(id);
        if (categoryEntity == null) {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.NOT_FOUND)
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Category not found!")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiRespone);
        }

        // Check category name
        CategoryEntity checkCategory = categoryService.findCategoryByCategoryName(categoryDto.getCategoryName());
        if (checkCategory != null) {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.IS_EXIST)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Category name is existed")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiRespone);
        }

        // Update Category
        CategoryEntity categoryUpdated = categoryService.updateCategory(categoryEntity, categoryDto);
        CategoryDto categoryDtoResponse = categoryMapper.toDto(categoryUpdated);
        ApiResponse<CategoryDto> apiRespone = ApiResponse.<CategoryDto>builder()
                .status(ApiStatus.UPDATED)
                .code(HttpStatus.OK.value())
                .message("Update category data success!")
                .metadata(Collections.emptyMap())
                .data(List.of(categoryDtoResponse))
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiRespone);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        // Kiem tra thong tin login cua user
        ShopUserDetail shopUserDetail = loginUltils.getCurrentUser();
        if (shopUserDetail == null) {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.UNAUTHORIZED)
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message("User chưa đăng nhập!")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiRespone);
        }

        // Check role and path
        if (!loginUltils.checkRolePass("/api/v1/categorys/", HttpMethod.DELETE)) {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.FORBIDDEN)
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("User chưa đăng nhập hoặc không có quyền truy cập!")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiRespone);
        }

        // Delete category
        CategoryEntity deleteCategory = categoryService.deleteCategory(id);
        if (deleteCategory == null) {
            ErrorResponse apiRespone = ErrorResponse.builder()
                    .status(ApiStatus.NOT_FOUND)
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Category not found!")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiRespone);
        }

        CategoryDto deleteCategoryDto = categoryMapper.toDto(deleteCategory);

        ApiResponse<CategoryDto> apiRespone = ApiResponse.<CategoryDto>builder()
                .status(ApiStatus.DELETED)
                .code(HttpStatus.OK.value())
                .message("Delete category data success!")
                .metadata(Collections.emptyMap())
                .data(List.of(deleteCategoryDto))
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiRespone);
    }
}
