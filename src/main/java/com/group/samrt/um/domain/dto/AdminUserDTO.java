package com.group.samrt.um.domain.dto;

import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;

@Data
public class AdminUserDTO {
   private Long id;

    private String username;
    private String password;
    private String role;       // ADMIN / MEMBER
    private String status;     // ACTIVE / INACTIVE
    private String fullname;
    private String email;
    private String phone;
    private String address;
    // ====== Quản lý hệ thống ======
    private String createdBy;
    private Instant createdDt;
    private String updatedBy;
    private Instant updatedDt;
}
