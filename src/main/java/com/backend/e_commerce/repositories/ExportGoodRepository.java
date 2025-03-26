package com.backend.e_commerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.e_commerce.domain.entities.ExportGoodEntity;

@Repository
public interface ExportGoodRepository extends JpaRepository<ExportGoodEntity, Long> {

}
