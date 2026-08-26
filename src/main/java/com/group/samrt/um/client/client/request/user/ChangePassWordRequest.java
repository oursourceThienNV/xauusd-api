package com.group.samrt.um.client.client.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePassWordRequest {
    private String oldPassword;
    private String newPassword;
    private String rePassword;
}
