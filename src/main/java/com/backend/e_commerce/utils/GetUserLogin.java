package com.backend.e_commerce.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.backend.e_commerce.controllers.ErrorController;
import com.backend.e_commerce.security.ShopUserDetail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserLogin {

    private final ErrorController errorController;

    public ShopUserDetail getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.error("Khong tim thay authentication trong SecurityContext");
            return null;
        }

        if (!(authentication.getPrincipal() instanceof ShopUserDetail)) {
            log.error("Principal khong phai kieu ShopUserDetail, ma la: " +
                    authentication.getPrincipal().getClass());
            return null;
        }

        return (ShopUserDetail) authentication.getPrincipal();
    }

    public static boolean checkRolePass(ShopUserDetail shopUserDetail, String path, String method) {
        // ✅ Lấy danh sách role của người dùng
        List<String> roles = shopUserDetail.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        for (String role : roles) {
            System.out.println(role);
        }
        return true;
    }
}
