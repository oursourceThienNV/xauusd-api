package com.group.samrt.um.client.client.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String error_code;

    // Thông tin license
    private Long licenseDays;
    private Instant licenseExpiredDt;
    private String username;
    private String accessToken;
    public LoginResponse(){}
}