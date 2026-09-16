package com.group.samrt.um.controller;

import com.group.samrt.um.client.Common.Constant;
import com.group.samrt.um.client.client.request.SystemLogRequest;
import com.group.samrt.um.client.client.request.user.LoginRequest;
import com.group.samrt.um.client.client.response.user.LoginResponse;
import com.group.samrt.um.domain.uml.AdminUser;
import com.group.samrt.um.domain.uml.TradingTransaction;
import com.group.samrt.um.filter.JwtTokenProvider;
import com.group.samrt.um.service.SystemLogService;
import com.group.samrt.um.service.TradingTransactionService;
import com.group.samrt.um.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
public class LodaRestController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private TradingTransactionService tradingTransactionService;
    @Autowired
    private SystemLogService systemLogService;
    @PostMapping("/login")
    public LoginResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();

        if (!loginRequest.getVersion().equals(Constant.VERSION.version)) {
            loginResponse.setError_code(Constant.ERROR_CODE.ERROR_VERSION);
            return loginResponse;
        }

        AdminUser check = userService.getUserName(loginRequest.getUsername());

        if (check == null) {
            loginResponse.setError_code(Constant.ERROR_CODE.ERROR_OTHER);
            return loginResponse;
        }

        if (check.getStatus().equals(Constant.STATUS.INACTICE)) {
            loginResponse.setError_code(Constant.ERROR_CODE.ERROR_INACTIVE);
            return loginResponse;
        }

// =====================================
// LOGIN SUCCESS
// =====================================

        loginResponse.setError_code(
                Constant.ERROR_CODE.ERROR_SUCCESS
        );
        loginResponse.setUsername(check.getUsername());

        loginResponse.setLicenseExpiredDt(
                check.getLicenseExpiredDt()
        );

        if (check.getLicenseExpiredDt() != null) {

            long licenseDays = ChronoUnit.DAYS.between(
                    Instant.now(),
                    check.getLicenseExpiredDt()
            );

            loginResponse.setLicenseDays(
                    Math.max(0, licenseDays)
            );

        } else {

            loginResponse.setLicenseDays(0L);
        }

        return loginResponse;
    }
    @PostMapping("/trading-transaction")
    public TradingTransaction saveTradingTransaction(
            @RequestBody TradingTransaction transaction) {
        return tradingTransactionService.save(transaction);
    }
    @PostMapping("/write-log")
    public boolean saveLog(
            @RequestBody SystemLogRequest request) {
        return systemLogService.save(request);
    }
    @GetMapping("/trading-transaction/{ticket}")
    public TradingTransaction getTradingTransaction(
            @PathVariable Long ticket
    ) {
        return tradingTransactionService.findByTicket(ticket);
    }
    @GetMapping("/check-result/{ticket}")
    public String checkResult(
            @PathVariable Long ticket
    ) {
        return tradingTransactionService.checkResult(ticket);
    }
    @PostMapping("/trading-transaction/save-history")
    public List<TradingTransaction> saveTradingTransactions(
            @RequestBody List<TradingTransaction> transactions) {

        return tradingTransactionService.saveAll(transactions);
    }
}
