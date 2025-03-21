package com.backend.e_commerce.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.e_commerce.domain.entities.ProductEntity;

import jakarta.validation.constraints.NotNull;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    ProductEntity findProductByProductCode(String productCode);

    ProductEntity findProductByProductId(Long id);

    @Query("SELECT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.variants " +
            "LEFT JOIN FETCH p.images " +
            "LEFT JOIN FETCH p.specificationMappings sm " +
            "LEFT JOIN FETCH sm.specification s " +
            "LEFT JOIN FETCH sm.specificationDetails sd " +
            "LEFT JOIN FETCH sd.specificationDetail " +
            "WHERE p.productId = :productId")
    Optional<ProductEntity> findProductById(@NotNull Long productId);
}
