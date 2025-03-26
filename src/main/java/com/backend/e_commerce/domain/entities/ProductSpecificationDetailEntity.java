package com.backend.e_commerce.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product_specification_detail")
public class ProductSpecificationDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_specification_mapping_id")
    private ProductSpecificationMappingEntity productSpecificationMapping;

    @ManyToOne
    @JoinColumn(name = "specification_detail_id")
    private SpecificationDetailEntity specificationDetail;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "is_highlighted", nullable = false)
    private boolean isHighlighted;
}
