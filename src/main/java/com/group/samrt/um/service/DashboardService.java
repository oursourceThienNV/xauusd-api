package com.group.samrt.um.service;


import com.group.samrt.um.client.client.response.BestConfigurationResponse;
import com.group.samrt.um.client.client.response.DashboardResponse;

import com.group.samrt.um.respository.AdminUserRepository;
import com.group.samrt.um.respository.TradingTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AdminUserRepository adminUserRepository;

    private final TradingTransactionRepository tradingTransactionRepository;


    // =====================================================
    // DASHBOARD
    // =====================================================

    public DashboardResponse getDashboard() {

        DashboardResponse response =
                new DashboardResponse();


        // =================================================
        // ACCOUNT
        // =================================================

        long totalAccounts =
                adminUserRepository.count();


        long activeAccounts =
                adminUserRepository.countByStatus("01");


        long blockedAccounts =
                adminUserRepository.countByStatus("00");


        long expiredAccounts =
                adminUserRepository.countExpiredAccounts();


        response.setTotalAccounts(
                totalAccounts
        );

        response.setActiveAccounts(
                activeAccounts
        );

        response.setBlockedAccounts(
                blockedAccounts
        );

        response.setExpiredAccounts(
                expiredAccounts
        );


        // =================================================
        // TRADING
        // =================================================

        Long totalTrades =
                tradingTransactionRepository
                        .countAllTrades();


        BigDecimal totalProfit =
                tradingTransactionRepository
                        .sumAllProfit();


        response.setTotalTrades(
                totalTrades != null
                        ? totalTrades
                        : 0L
        );


        response.setTotalProfit(
                totalProfit != null
                        ? totalProfit
                        : BigDecimal.ZERO
        );


        // =================================================
        // BALANCE
        // =================================================

        /*
         * Balance hiện tại:
         *
         * Lấy closeBalance mới nhất
         * của từng account rồi cộng lại.
         */

        BigDecimal totalBalance =
                calculateTotalBalance();


        response.setTotalBalance(
                totalBalance
        );


        // =================================================
        // BEST CONFIGURATION
        // =================================================

        BestConfigurationResponse bestConfiguration =
                getBestConfiguration();


        response.setBestConfiguration(
                bestConfiguration
        );


        return response;
    }


    // =====================================================
    // TOTAL BALANCE
    // =====================================================

    private BigDecimal calculateTotalBalance() {

        List<String> accounts =
                tradingTransactionRepository
                        .findDistinctAccounts();


        BigDecimal total =
                BigDecimal.ZERO;


        for (String account : accounts) {

            List<BigDecimal> balances =
                    tradingTransactionRepository
                            .findLatestBalance(
                                    account,
                                    PageRequest.of(0, 1)
                            );


            if (
                    balances != null &&
                            !balances.isEmpty() &&
                            balances.get(0) != null
            ) {

                total =
                        total.add(
                                balances.get(0)
                        );
            }
        }


        return total;
    }


    // =====================================================
    // BEST CONFIGURATION
    // =====================================================

    private BestConfigurationResponse getBestConfiguration() {

        List<Object[]> results =
                tradingTransactionRepository
                        .findBestConfiguration(
                                PageRequest.of(0, 1)
                        );


        if (
                results == null ||
                        results.isEmpty()
        ) {

            return null;
        }


        Object[] row =
                results.get(0);


        BestConfigurationResponse response =
                new BestConfigurationResponse();


        // =================================================
        // CONFIGURATION
        // =================================================

        response.setStrategy(
                (String) row[0]
        );


        response.setMultiLot(
                (Boolean) row[1]
        );


        response.setMaFast(
                row[2] != null
                        ? ((Number) row[2]).intValue()
                        : null
        );


        response.setMaSlow(
                row[3] != null
                        ? ((Number) row[3]).intValue()
                        : null
        );


        response.setMaTrendValue(
                row[4] != null
                        ? ((Number) row[4]).intValue()
                        : null
        );


        response.setSideway(
                (Boolean) row[5]
        );


        response.setFomo(
                (Boolean) row[6]
        );


        // =================================================
        // STATISTICS
        // =================================================

        Long totalTrades =
                row[7] != null
                        ? ((Number) row[7]).longValue()
                        : 0L;


        BigDecimal totalProfit =
                row[8] != null
                        ? (BigDecimal) row[8]
                        : BigDecimal.ZERO;


        Long winTrades =
                row[9] != null
                        ? ((Number) row[9]).longValue()
                        : 0L;


        response.setTotalTrades(
                totalTrades
        );


        response.setTotalProfit(
                totalProfit
        );


        // =================================================
        // WIN RATE
        // =================================================

        BigDecimal winRate =
                BigDecimal.ZERO;


        if (totalTrades > 0) {

            winRate =
                    BigDecimal.valueOf(
                            winTrades * 100.0 /
                                    totalTrades
                    );

        }


        response.setWinRate(
                winRate
        );


        return response;
    }
}