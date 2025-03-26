package com.backend.e_commerce.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.backend.e_commerce.domain.entities.UserEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ShopUserDetail implements UserDetails {

    final private UserEntity user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getUserRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName())) // Chuyển thành GrantedAuthority
                .collect(Collectors.toList()); // Trả về List thay vì Set
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    public Long getUserId() {
        return user.getId();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

}
