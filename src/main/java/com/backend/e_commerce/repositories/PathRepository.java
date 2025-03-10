package com.backend.e_commerce.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.e_commerce.domain.entities.PathEntity;

@Repository
public interface PathRepository extends JpaRepository<PathEntity, Integer> {
    Optional<PathEntity> findByUrl(String url);
}
