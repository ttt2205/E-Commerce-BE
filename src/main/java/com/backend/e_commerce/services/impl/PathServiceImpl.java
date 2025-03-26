package com.backend.e_commerce.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.entities.PathEntity;
import com.backend.e_commerce.repositories.PathRepository;
import com.backend.e_commerce.services.PathService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PathServiceImpl implements PathService {

    private final PathRepository pathRepository;

    @Override
    public Optional<PathEntity> checkPathIsExist(String url) {
        return pathRepository.findByUrl(url);
    }

}
