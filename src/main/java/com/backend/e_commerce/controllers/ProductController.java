package com.backend.e_commerce.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.backend.e_commerce.domain.ApiStatus;
import com.backend.e_commerce.domain.dtos.ApiRespone;
import com.backend.e_commerce.domain.dtos.ProductDto;
import com.backend.e_commerce.domain.dtos.ProductRequest;
import com.backend.e_commerce.domain.entities.ProductEntity;
import com.backend.e_commerce.mappers.ProductMapper;
import com.backend.e_commerce.security.ShopUserDetail;
import com.backend.e_commerce.services.ProductService;
import com.backend.e_commerce.utils.GetUserLogin;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    private final GetUserLogin getUserLogin;

    @GetMapping
    public ResponseEntity<ApiRespone<ProductDto>> getProductList() {
        List<ProductEntity> results = productService.findAll();
        List<ProductDto> productDtos = results.stream()
                .map(productMapper::toDto)
                .toList();
        ApiRespone<ProductDto> apiRespone = ApiRespone.<ProductDto>builder()
                .status(ApiStatus.SUCCESS)
                .message("Get products data success!")
                .metadata(ApiRespone.Metadata.builder().count(productDtos.size()).build())
                .data(productDtos)
                .build();
        return ResponseEntity.ok(apiRespone);
    }

    @PostMapping
    public ResponseEntity<?> postCreateNewProduct(@RequestBody ProductRequest productRequest) {
        ShopUserDetail shopUserDetail = getUserLogin.getCurrentUser();
        if (shopUserDetail == null) {
        ApiRespone<ProductDto> apiRespone = ApiRespone.<ProductDto>builder()
        .status(ApiStatus.SUCCESS)
        .message("User chưa đăng nhập!")
        .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiRespone);
        }

        if (productService.createNewProductItem(productRequest)) {
            ProductEntity productEntity = productService.findProductByProductCode(productRequest.getProductCode());
            ProductDto productDto = productMapper.toDto(productEntity);
            List<ProductDto> listProductDtos = new ArrayList<>();
            listProductDtos.add(productDto);
            ApiRespone<ProductDto> apiRespone = ApiRespone.<ProductDto>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Create product data success!")
                    .metadata(ApiRespone.Metadata.builder().count(listProductDtos.size()).build())
                    .data(listProductDtos)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(apiRespone);
        } else {
            ApiRespone<Void> apiRespone = ApiRespone.<Void>builder()
                    .status(ApiStatus.FAIL)
                    .message("Failed to create product. Please check your input data.")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiRespone);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiRespone<?>> getMethodName(@PathVariable String id) {
        Long productId = Long.parseLong(id);
        ProductEntity productEntity = productService.findProductByProductId(productId);
        if (productEntity != null) {
            ProductDto productDto = productMapper.toDto(productEntity);
            List<ProductDto> listProductDtos = new ArrayList<>();
            listProductDtos.add(productDto);
            ApiRespone<ProductDto> apiRespone = ApiRespone.<ProductDto>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Get product data success!")
                    .metadata(ApiRespone.Metadata.builder().count(listProductDtos.size()).build())
                    .data(listProductDtos)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(apiRespone);
        } else {
            ApiRespone<Void> apiRespone = ApiRespone.<Void>builder()
                    .status(ApiStatus.NOT_FOUND)
                    .message("Get product data fail!")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiRespone);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiRespone<?>> patchProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest productRequest) {
        ProductEntity productEntity = productService.findProductByProductId(id);
        if (productEntity == null) {
            ApiRespone<Void> apiRespone = ApiRespone.<Void>builder()
                    .status(ApiStatus.NOT_FOUND)
                    .message("Product not found!")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiRespone);
        }
        ProductEntity productUpdated = productService.updateParticalProduct(productEntity, productRequest);
        ProductDto productDto = productMapper.toDto(productUpdated);
        List<ProductDto> listProductDtos = new ArrayList<>();
        listProductDtos.add(productDto);
        ApiRespone<ProductDto> apiRespone = ApiRespone.<ProductDto>builder()
                .status(ApiStatus.UPDATED)
                .message("Update product data success!")
                .metadata(ApiRespone.Metadata.builder().count(listProductDtos.size()).build())
                .data(listProductDtos)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiRespone);
    }
}
