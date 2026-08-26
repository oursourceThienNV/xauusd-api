package com.group.samrt.um.client.client.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BestConfigurationResponse {

    /**
     * LOW_PROFIT / HIGH_PROFIT
     */
    private String strategy;


    /**
     * Có sử dụng Multi Lot
     */
    private Boolean multiLot;


    /**
     * MA Cross Fast
     */
    private Integer maFast;


    /**
     * MA Cross Slow
     */
    private Integer maSlow;


    /**
     * MA Trend
     */
    private Integer maTrendValue;


    /**
     * Sideway
     */
    private Boolean sideway;


    /**
     * FOMO
     */
    private Boolean fomo;


    /**
     * Tổng số giao dịch
     */
    private Long totalTrades;


    /**
     * Tổng lợi nhuận
     */
    private BigDecimal totalProfit;


    /**
     * Win Rate
     */
    private BigDecimal winRate;
}