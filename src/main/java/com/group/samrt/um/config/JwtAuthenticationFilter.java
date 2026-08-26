package com.group.samrt.um.config;

import com.group.samrt.um.client.Common.Constant;
import com.group.samrt.um.domain.uml.AdminUser;
import com.group.samrt.um.filter.JwtTokenProvider;
import com.group.samrt.um.respository.AdminUserRepository;
import com.group.samrt.um.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService customUserDetailsService;
    @Autowired
    private AdminUserRepository adminUserRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Lấy jwt từ request
            String path = request.getRequestURI();
            if (path.startsWith("/ws/")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                // Lấy username từ token
                String userName = tokenProvider.getUserNameFromJWT(jwt);

                // Kiểm tra tài khoản có tồn tại không
                Optional<AdminUser> optionalUser = adminUserRepository.findAdminUserByUsername(userName);
                if (optionalUser.isEmpty()) {
                    filterChain.doFilter(request, response);
                    return; // Không tìm thấy user, kết thúc
                }

                AdminUser user = optionalUser.get();

                // Kiểm tra trạng thái tài khoản
                if (Constant.STATUS.INACTICE.equals(user.getStatus())) {
                    filterChain.doFilter(request, response);
                    return; // Tài khoản bị khóa, kết thúc
                }
                // Load thông tin người dùng từ hệ thống
                UserDetails adminUser = customUserDetailsService.loadUserByUsername(userName);
                if (adminUser != null) {

                    // Tạo đối tượng xác thực cho SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            log.error("failed on set user authentication", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
