package com.group.samrt.um.client.client.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountListResponse {

    private String account;

    /**
     * Số dư hiện tại
     */
    private BigDecimal balance;

    /**
     * Tổng lợi nhuận
     */
    private BigDecimal profit;

    /**
     * Tổng số giao dịch
     */
    private Long totalTrades;

    /**
     * Số giao dịch thắng
     */
    private Long winTrades;

    /**
     * Win rate %
     */
    private BigDecimal winRate;

    /**
     * Trạng thái quản lý
     */
    private String status;

    /**
     * Hạn sử dụng
     */
    private String licenseExpiredDt;
    private Long remainingDays;
}