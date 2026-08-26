package com.group.samrt.um.client.client.response.user;

import com.group.samrt.um.client.Common.Util.OptimizedPage;
import com.group.samrt.um.domain.dto.AdminUserDTO;
import com.group.samrt.um.domain.dto.UserDetailDTO;
import com.group.samrt.um.domain.uml.AdminUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private OptimizedPage<AdminUser> page;
    private String errorCode;
    public UserResponse() {

    }
}
