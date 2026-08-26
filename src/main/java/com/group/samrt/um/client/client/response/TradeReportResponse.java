package com.group.samrt.um.client.client.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TradeReportResponse {

    private Long id;

    private Long ticket;

    // ACCOUNT
    private String account;

    // TRADE
    private String type;

    private BigDecimal lot;

    private BigDecimal openPrice;

    private BigDecimal closePrice;

    private Instant openTime;

    private Instant closeTime;

    private BigDecimal profit;

    private String tradeStatus;

    private String result;

    // ACCOUNT BALANCE
    private BigDecimal openBalance;

    private BigDecimal closeBalance;

    // BOT CONFIGURATION
    private String strategy;

    private Boolean multiLot;

    private String lotChain;

    // RR
    private BigDecimal rrRisk;

    private BigDecimal rrReward;

    // MA
    private Integer maFast;

    private Integer maSlow;

    private Integer maTrendValue;

    // FILTER
    private Boolean sideway;

    private Boolean fomo;

    // SYNC
    private String syncStatus;

    private Instant syncedDt;

    // SYSTEM
    private Instant createdDt;

    private Instant updatedDt;

    private Boolean firstTradeAfterBotStart;
}