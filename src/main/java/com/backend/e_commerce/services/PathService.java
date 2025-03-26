package com.backend.e_commerce.services;

import java.util.Optional;

import com.backend.e_commerce.domain.entities.PathEntity;

public interface PathService {

    public Optional<PathEntity> checkPathIsExist(String url);
}
