package com.group.samrt.um.client.client.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardResponse {

    // =====================================================
    // ACCOUNT
    // =====================================================

    private Long totalAccounts;

    private Long activeAccounts;

    private Long blockedAccounts;

    private Long expiredAccounts;


    // =====================================================
    // FINANCIAL
    // =====================================================

    private BigDecimal totalBalance;

    private BigDecimal totalProfit;

    private Long totalTrades;


    // =====================================================
    // BEST CONFIGURATION
    // =====================================================

    private BestConfigurationResponse bestConfiguration;
}