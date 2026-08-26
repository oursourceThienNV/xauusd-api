package com.group.samrt.um.client.client.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountReportResponse {

    private String account;

    /**
     * Số dư hiện tại
     */
    private BigDecimal balance;

    /**
     * Tổng lợi nhuận trong khoảng thời gian
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
     * Win Rate
     */
    private BigDecimal winRate;

    /**
     * 01 = hoạt động
     * 00 = khóa
     */
    private String status;

    /**
     * Ngày hết hạn license
     */
    private String licenseExpiredDt;

    /**
     * Số ngày còn lại
     */
    private Long remainingDays;
}