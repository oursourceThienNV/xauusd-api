package com.group.samrt.um.service;

import com.group.samrt.um.client.Common.Constant;
import com.group.samrt.um.client.client.response.AccountListResponse;
import com.group.samrt.um.domain.uml.AdminUser;
import com.group.samrt.um.respository.AdminUserRepository;
import com.group.samrt.um.respository.TradingTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = {Exception.class})
public class UserService implements UserDetailsService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private TradingTransactionRepository tradingTransactionRepository;


    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    public UserDetails loadUserByUsername(String username) {

        AdminUser user =
                adminUserRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new CustomUserDetail(user);
    }


    public AdminUser getUserName(String username) {

        AdminUser user =
                adminUserRepository.findByUsername(username);

        if (user != null) {
            user.setUpdatedDt(Instant.now());
            adminUserRepository.save(user);
        }

        return user;
    }


    public AdminUser getByUserName(String username) {

        Optional<AdminUser> adminUser =
                adminUserRepository.findByUsernameAndStatus(
                        username,
                        Constant.STATUS.ACTIVE
                );

        return adminUser.orElse(null);
    }


    // =========================================================
    // ACCOUNT MANAGEMENT
    // =========================================================

    /**
     * Danh sách tài khoản MT5
     *
     * Có phân trang tại DB.
     */
    @Transactional(readOnly = true)
    public Page<AccountListResponse> getAccountList(
            String keyword,
            Pageable pageable
    ) {

        Page<AdminUser> userPage =
                adminUserRepository.findAccountList(
                        keyword,
                        pageable
                );

        return userPage.map(user ->
                buildAccountResponse(
                        user.getUsername(),
                        user.getStatus(),
                        user.getLicenseExpiredDt()
                )
        );
    }


    /**
     * Build thông tin account.
     */
    private AccountListResponse buildAccountResponse(
            String account,
            String status,
            Instant licenseExpiredDt
    ) {

        AccountListResponse response =
                new AccountListResponse();

        response.setAccount(account);


        // =========================================
        // BALANCE
        // =========================================

        List<BigDecimal> balances =
                tradingTransactionRepository.findLatestBalance(
                        account,
                        org.springframework.data.domain.PageRequest.of(
                                0,
                                1
                        )
                );

        BigDecimal balance =
                balances.isEmpty()
                        ? BigDecimal.ZERO
                        : balances.get(0);

        response.setBalance(balance);


        // =========================================
        // PROFIT
        // =========================================

        BigDecimal profit =
                tradingTransactionRepository.findTotalProfit(
                        account
                );

        response.setProfit(
                profit != null
                        ? profit
                        : BigDecimal.ZERO
        );


        // =========================================
        // TOTAL TRADES
        // =========================================

        Long totalTrades =
                tradingTransactionRepository.countTrades(
                        account
                );

        response.setTotalTrades(
                totalTrades != null
                        ? totalTrades
                        : 0L
        );


        // =========================================
        // WIN TRADES
        // =========================================

        Long winTrades =
                tradingTransactionRepository.countWinTrades(
                        account
                );

        response.setWinTrades(
                winTrades != null
                        ? winTrades
                        : 0L
        );


        // =========================================
        // WIN RATE
        // =========================================

        if (totalTrades != null && totalTrades > 0) {

            BigDecimal winRate =
                    BigDecimal.valueOf(
                            winTrades * 100.0 / totalTrades
                    );

            response.setWinRate(
                    winRate.setScale(
                            2,
                            java.math.RoundingMode.HALF_UP
                    )
            );

        } else {

            response.setWinRate(
                    BigDecimal.ZERO
            );
        }


        // =========================================
        // STATUS
        // =========================================

        response.setStatus(status);


        // =========================================
        // LICENSE
        // =========================================

        if (licenseExpiredDt != null) {
            response.setLicenseExpiredDt(
                    licenseExpiredDt.toString()
            );
        } else {
            response.setLicenseExpiredDt(null);
        }
        long remainingDays = 0;

        if (licenseExpiredDt != null) {
            remainingDays = java.time.temporal.ChronoUnit.DAYS.between(
                    java.time.Instant.now(),
                    licenseExpiredDt
            );

            if (remainingDays < 0) {
                remainingDays = 0;
            }
        }

        response.setRemainingDays(remainingDays);

        return response;
    }
    // =========================================================
// BLOCK ACCOUNT
// =========================================================

    public AdminUser blockAccount(String username) {

        AdminUser user =
                adminUserRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException(
                    "Tài khoản không tồn tại: " + username
            );
        }

        user.setStatus("00");
        user.setUpdatedDt(Instant.now());

        return adminUserRepository.save(user);
    }


// =========================================================
// UNBLOCK ACCOUNT
// =========================================================

    public AdminUser unblockAccount(String username) {

        AdminUser user =
                adminUserRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException(
                    "Tài khoản không tồn tại: " + username
            );
        }

        user.setStatus("01");
        user.setUpdatedDt(Instant.now());

        return adminUserRepository.save(user);
    }


// =========================================================
// RENEW LICENSE
// =========================================================

    public AdminUser renewAccount(
            String username,
            int days
    ) {

        AdminUser user =
                adminUserRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException(
                    "Tài khoản không tồn tại: " + username
            );
        }

        if (days <= 0) {
            throw new RuntimeException(
                    "Số ngày gia hạn phải lớn hơn 0."
            );
        }

        Instant now = Instant.now();

        Instant currentExpired =
                user.getLicenseExpiredDt();

        /*
         * Nếu license hiện tại vẫn còn hạn:
         *      ngày hết hạn mới = ngày hết hạn cũ + days
         *
         * Nếu đã hết hạn hoặc chưa từng có license:
         *      ngày hết hạn mới = hiện tại + days
         */
        Instant baseDate;

        if (currentExpired != null
                && currentExpired.isAfter(now)) {

            baseDate = currentExpired;

        } else {

            baseDate = now;
        }

        Instant newExpiredDate =
                baseDate.plus(
                        days,
                        java.time.temporal.ChronoUnit.DAYS
                );

        user.setLicenseExpiredDt(
                newExpiredDate
        );

        /*
         * Gia hạn thành công thì mở lại account.
         */
        user.setStatus(Constant.STATUS.ACTIVE);

        user.setUpdatedDt(now);

        return adminUserRepository.save(user);
    }
}