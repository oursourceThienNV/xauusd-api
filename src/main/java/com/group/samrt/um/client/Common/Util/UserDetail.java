package com.group.samrt.um.client.Common.Util;

import com.group.samrt.um.domain.dto.UserDetailDTO;
import com.group.samrt.um.domain.uml.AdminUser;
import com.group.samrt.um.respository.AdminUserRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = {Exception.class, ServiceException.class})
public class UserDetail {
    @Autowired
    AdminUserRepository adminUserRepository;


    public UserDetailDTO getUserDetail() {
        UserDetailDTO userDetail = new UserDetailDTO();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AdminUser user = adminUserRepository.findByUsername(userDetails.getUsername());

        userDetail.setId(user.getId());
        userDetail.setUsername(user.getUsername());
        return userDetail;
    }
}
