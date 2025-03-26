package com.backend.e_commerce.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.backend.e_commerce.domain.ApiStatus;
import com.backend.e_commerce.domain.HttpMethod;
import com.backend.e_commerce.domain.dtos.ApiResponse;
import com.backend.e_commerce.domain.dtos.ErrorResponse;
import com.backend.e_commerce.domain.dtos.ProductDto;
import com.backend.e_commerce.domain.dtos.ProductRequest;
import com.backend.e_commerce.domain.entities.ProductEntity;
import com.backend.e_commerce.domain.entities.UserEntity;
import com.backend.e_commerce.mappers.ProductMapper;
import com.backend.e_commerce.security.ShopUserDetail;
import com.backend.e_commerce.services.ProductService;
import com.backend.e_commerce.utils.CheckUtils;
import com.backend.e_commerce.utils.LoginUtils;
import com.backend.e_commerce.utils.UrlParamUtils;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.util.MultiValueMap;

@RestController
@RequestMapping("/api/v1/products/")
@RequiredArgsConstructor
public class ProductController {

        private final ProductService productService;

        private final ProductMapper productMapper;

        private final LoginUtils loginUltils;

        private final CheckUtils checkUtils;

        private final UrlParamUtils urlParamUtils;

        @GetMapping
        public ResponseEntity<ApiResponse<ProductDto>> getProductList(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam MultiValueMap<String, String> params) {

                List<String> sortParams = params.get("sort"); // Lấy danh sách tham số `sort` co dang:
                                                              // [[id,asc],[name,desc]]
                Sort sorting = urlParamUtils.getSort(sortParams);
                Pageable pageable = PageRequest.of(page, size, sorting);
                Page<ProductEntity> productPage = productService.findAll(pageable);

                // Lay danh sach da loc
                List<ProductEntity> results = productPage.getContent();
                List<ProductDto> productDtos = results.stream()
                                .map(productMapper::toDto)
                                .toList();
                ApiResponse<ProductDto> apiRespone = ApiResponse.<ProductDto>builder()
                                .status(ApiStatus.SUCCESS)
                                .message("Get products data success!")
                                .metadata(Map.of(
                                                "totalElements", productPage.getTotalElements(),
                                                "totalPage", productPage.getTotalPages(),
                                                "elementOfPage", productPage.getNumberOfElements(), // so phan tu cua
                                                                                                    // trang hien tai
                                                "currentPage", productPage.getNumber()))
                                .data(productDtos)
                                .build();
                return ResponseEntity.ok(apiRespone);
        }

        @PostMapping
        public ResponseEntity<?> postCreateNewProduct(@RequestBody ProductRequest productRequest) {
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
                if (!loginUltils.checkRolePass("/api/v1/products/", HttpMethod.POST)) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.FORBIDDEN)
                                        .code(HttpStatus.FORBIDDEN.value())
                                        .message("User chưa đăng nhập hoặc không có quyền truy cập!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiRespone);
                }

                // Check product status
                if (!checkUtils.checkValidProductStatus(productRequest.getStatus())) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.BAD_REQUEST)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Invalid product status!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiRespone);
                }

                // Tao moi product
                if (productService.createNewProductItem(productRequest)) {
                        ProductEntity productEntity = productService
                                        .findProductByProductCode(productRequest.getProductCode());
                        ProductDto productDto = productMapper.toDto(productEntity);
                        ApiResponse<ProductDto> apiRespone = ApiResponse.<ProductDto>builder()
                                        .status(ApiStatus.SUCCESS)
                                        .message("Create product data success!")
                                        .code(HttpStatus.CREATED.value())
                                        .metadata(Collections.emptyMap())
                                        .data(List.of(productDto))
                                        .build();
                        return ResponseEntity.status(HttpStatus.CREATED).body(apiRespone);
                } else {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.BAD_REQUEST)
                                        .message("Failed to create product! Please check your input data or product is exist.")
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(apiRespone);
                }
        }

        @GetMapping("{id}")
        public ResponseEntity<?> getMethodName(@PathVariable String id) {
                Long productId = Long.parseLong(id);
                ProductEntity productEntity = productService.findProductById(productId);
                if (productEntity != null) {
                        ProductDto productDto = productMapper.toDto(productEntity);
                        ApiResponse<ProductDto> apiRespone = ApiResponse.<ProductDto>builder()
                                        .status(ApiStatus.SUCCESS)
                                        .code(HttpStatus.OK.value())
                                        .message("Get product data success!")
                                        .metadata(Collections.emptyMap())
                                        .data(List.of(productDto))
                                        .build();
                        return ResponseEntity.status(HttpStatus.OK).body(apiRespone);
                } else {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.NOT_FOUND)
                                        .code(HttpStatus.NOT_FOUND.value())
                                        .message("Product not found!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(apiRespone);
                }
        }

        @PatchMapping("{id}")
        public ResponseEntity<?> patchProduct(
                        @PathVariable Long id,
                        @RequestBody ProductRequest productRequest) {
                // Kiem tra thong tin login cua user
                ShopUserDetail shopUserDetail = loginUltils.getCurrentUser();
                if (shopUserDetail == null) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.UNAUTHORIZED)
                                        .code(HttpStatus.UNAUTHORIZED.value())
                                        .message("User chưa đăng nhập!")
                                        .build();

                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(apiRespone);
                }

                // Check role and path
                if (!loginUltils.checkRolePass("/api/v1/products/", HttpMethod.PATCH)) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.FORBIDDEN)
                                        .code(HttpStatus.FORBIDDEN.value())
                                        .message("User chưa đăng nhập hoặc không có quyền truy cập!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiRespone);
                }

                // Check product
                ProductEntity productEntity = productService.findProductById(id);
                if (productEntity == null) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.NOT_FOUND)
                                        .code(HttpStatus.NOT_FOUND.value())
                                        .message("Product not found!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiRespone);
                }

                // Check product status
                if (!checkUtils.checkValidProductStatus(productRequest.getStatus())) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.BAD_REQUEST)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Invalid product status!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiRespone);
                }

                // Update Product
                ProductEntity productUpdated = productService.updateParticalProduct(productEntity, productRequest);
                ProductDto productDto = productMapper.toDto(productUpdated);
                ApiResponse<ProductDto> apiRespone = ApiResponse.<ProductDto>builder()
                                .status(ApiStatus.UPDATED)
                                .code(HttpStatus.OK.value())
                                .message("Update product' status data success!")
                                .metadata(Collections.emptyMap())
                                .data(List.of(productDto))
                                .build();
                return ResponseEntity.status(HttpStatus.OK).body(apiRespone);
        }

        @PutMapping("{id}")
        public ResponseEntity<?> putProduct(
                        @PathVariable Long id,
                        @RequestBody ProductRequest productRequest) {
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
                if (!loginUltils.checkRolePass("/api/v1/products/", HttpMethod.PUT)) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.FORBIDDEN)
                                        .code(HttpStatus.FORBIDDEN.value())
                                        .message("User chưa đăng nhập hoặc không có quyền truy cập!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiRespone);
                }

                // Check product
                ProductEntity productEntity = productService.findProductById(id);
                if (productEntity == null) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.NOT_FOUND)
                                        .code(HttpStatus.NOT_FOUND.value())
                                        .message("Product not found!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiRespone);
                }

                // Check product status
                if (!checkUtils.checkValidProductStatus(productRequest.getStatus())) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.BAD_REQUEST)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Invalid product status!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiRespone);
                }

                // Check product code
                ProductEntity checkProduct = productService.findProductByProductCode(productRequest.getProductCode());
                if (checkProduct != null) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.IS_EXIST)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Product code is existed")
                                        .build();
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiRespone);
                }

                // Update Product
                ProductEntity productUpdated = productService.updateProduct(productEntity, productRequest);
                ProductDto productDto = productMapper.toDto(productUpdated);
                ApiResponse<ProductDto> apiRespone = ApiResponse.<ProductDto>builder()
                                .status(ApiStatus.UPDATED)
                                .code(HttpStatus.OK.value())
                                .message("Update product data success!")
                                .metadata(Collections.emptyMap())
                                .data(List.of(productDto))
                                .build();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(apiRespone);
        }

        @DeleteMapping("{id}")
        public ResponseEntity<?> deleteProduct(
                        @PathVariable Long id) {
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
                if (!loginUltils.checkRolePass("/api/v1/products/", HttpMethod.DELETE)) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.FORBIDDEN)
                                        .code(HttpStatus.FORBIDDEN.value())
                                        .message("User chưa đăng nhập hoặc không có quyền truy cập!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiRespone);
                }

                // Delete product
                ProductEntity deleteProduct = productService.deleteProduct(id);
                if (deleteProduct == null) {
                        ErrorResponse apiRespone = ErrorResponse.builder()
                                        .status(ApiStatus.NOT_FOUND)
                                        .code(HttpStatus.NOT_FOUND.value())
                                        .message("Product not found!")
                                        .build();
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiRespone);
                }

                ProductDto deleteProductDto = productMapper.toDto(deleteProduct);

                ApiResponse<ProductDto> apiRespone = ApiResponse.<ProductDto>builder()
                                .status(ApiStatus.DELETED)
                                .code(HttpStatus.OK.value())
                                .message("Delete product data success!")
                                .metadata(Collections.emptyMap())
                                .data(List.of(deleteProductDto))
                                .build();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(apiRespone);
        }
}
