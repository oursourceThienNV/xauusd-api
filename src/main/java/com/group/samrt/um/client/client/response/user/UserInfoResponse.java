package com.group.samrt.um.client.client.response.user;

import com.group.samrt.um.domain.dto.UserDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private UserDetailDTO userInfo;

    public UserInfoResponse() {
    }
}
