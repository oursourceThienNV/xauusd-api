package com.group.samrt.um.service;

import com.group.samrt.um.client.Common.Constant;
import com.group.samrt.um.client.client.request.SystemLogRequest;
import com.group.samrt.um.domain.uml.AdminUser;
import com.group.samrt.um.respository.AdminUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class SystemLogService {
    @Autowired
    AdminUserRepository adminUserRepository;
    public boolean save(SystemLogRequest request) {
        log.info("Time: "+Instant.now()+"account: "+request.getAccount()+" "+ request.getLogs());
        AdminUser check = adminUserRepository.findByUsername(request.getAccount());
        if (check == null) {
            return false;
        }
        if (check.getStatus().equals(Constant.STATUS.INACTICE)) {
            return false;
        }
        return true;

    }

}
