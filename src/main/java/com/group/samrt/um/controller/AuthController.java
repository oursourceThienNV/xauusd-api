package com.group.samrt.um.controller;

import com.group.samrt.um.client.Common.Constant;
import com.group.samrt.um.client.client.request.user.LoginRequest;
import com.group.samrt.um.client.client.response.user.LoginResponse;
import com.group.samrt.um.domain.uml.AdminUser;
import com.group.samrt.um.filter.JwtTokenProvider;
import com.group.samrt.um.service.CustomUserDetail;
import com.group.samrt.um.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AdminUser user = userService.getByUserName(request.getUsername());

        if (user == null) {
            throw new RuntimeException("Người dùng không tồn tại.");
        }

        if (Constant.STATUS.INACTICE.equals(user.getStatus())) {
            throw new RuntimeException("Tài khoản đã bị khóa.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(
                (CustomUserDetail) authentication.getPrincipal()
        );

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
//      response.setRefreshToken(refreshToken); // sau này bổ sung

        return ResponseEntity.ok(response);
    }

    public static void main(String[] args) {
        String hashedPassword = BCrypt.hashpw("12345", BCrypt.gensalt());
        System.out.println(hashedPassword);

    }
}
