package com.group.samrt.um.domain.dto;

import com.group.samrt.um.domain.uml.AdminUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNoPasswordDTO {
    private Long id;
    private String username;
    private String fullname;
    private String role;
    private String email;
    private String phone;
    private String address;
    private String accountType;
    private Long groupTeamId;
    private String description;
    private Long companyId;
    private String companyName;
    private String groupName;
    private String idCardNo;
    private String logoUrl;
    private Long logoId;
    public UserNoPasswordDTO(AdminUser user){
        this.id=user.getId();
        this.username=user.getUsername();
        this.fullname=user.getFullname();
        this.role=user.getRole();
        this.email=user.getEmail();
        this.phone=user.getPhone();
        this.address=user.getAddress();
    }
}
