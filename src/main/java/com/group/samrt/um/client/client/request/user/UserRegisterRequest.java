package com.group.samrt.um.client.client.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {

    private Long id;

    // ====== Thông tin đăng nhập ======
    private String username;
    private String password;
    private String role;       // ADMIN / MEMBER
    private String status;     // ACTIVE / INACTIVE
    // ====== Thông tin cá nhân ======
    private String fullname;
    private String gender;     // NAM / NỮ
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private String generation;
    @Column(length = 2000)
    private String description;
    // ====== Huyết thống ======
    private Long fatherId;
    private Long motherId;
    private String maritalStatus;
    // ====== Vị trí bản đồ ======
    private Double latitude;
    private Double longitude;
    private String urlGoogleMap;

    // ====== Quản lý hệ thống ======
    private String createdBy;
    private Instant createdDt;
    private String updatedBy;
    private Instant updatedDt;
    private String roleInFamily;
    private List<Long> spouses;
    private String biography;
}
