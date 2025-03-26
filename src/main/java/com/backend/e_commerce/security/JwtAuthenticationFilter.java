package com.backend.e_commerce.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.backend.e_commerce.services.AuthenticationService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null) {
                Object user = authenticationService.validateToken(token);

                if (!(user instanceof UserDetails)) {
                    System.out.println("validateToken() không trả về UserDetails! Nhận được: " + user.getClass());
                    throw new RuntimeException("Invalid UserDetails");
                }

                UserDetails userDetails = (UserDetails) user;

                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                if (authorities == null) {
                    authorities = new ArrayList<>(); // Tránh lỗi khi authorities là null
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (userDetails instanceof ShopUserDetail) {
                    request.setAttribute("userId", ((ShopUserDetail) userDetails).getUserId());
                }
            }
        } catch (Exception e) {
            log.warn("Received invalid auth token", e);
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
