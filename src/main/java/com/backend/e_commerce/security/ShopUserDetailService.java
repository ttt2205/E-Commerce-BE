package com.backend.e_commerce.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.backend.e_commerce.domain.entities.UserEntity;
import com.backend.e_commerce.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional // ✅ Đảm bảo Hibernate session còn mở
public class ShopUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)

                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        /*
         * Ép Hibernate tải các danh sách liên quan của User trước khi session bị đóng
         * do dùng FetchType.LAZY
         * tránh lỗi khi gọi loadUserByUsername(String email) không tải được các fields
         * liên quan
         */
        // ✅ Truy cập userRoles để nạp dữ liệu trước khi session đóng
        userEntity.getUserRoles().size();
        // ✅ Nạp trước danh sách `orders`
        userEntity.getOrders().size();
        // ✅ Ép Hibernate tải danh sách wishLists trước khi session đóng
        userEntity.getWishLists().size();

        return new ShopUserDetail(userEntity);
    }

}
