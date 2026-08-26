package com.group.samrt.um.domain.uml;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "user")
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ====== Thông tin đăng nhập ======
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
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
    private Instant licenseStartDt;
    private Instant licenseExpiredDt;

}
