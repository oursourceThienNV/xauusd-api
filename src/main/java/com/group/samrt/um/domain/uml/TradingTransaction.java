package com.group.samrt.um.domain.uml;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "trading_transaction")
public class TradingTransaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ticket", unique = true, nullable = false)
    private Long ticket;
    // =====================================
    // ACCOUNT
    // =====================================

    /**
     * Tài khoản MT5
     */
    private String account;

    // =====================================
    // TRADE
    // =====================================

    /**
     * BUY / SELL
     */
    private String type;

    /**
     * Lot thực tế của lệnh
     */
    private BigDecimal lot;

    /**
     * Giá mở lệnh
     */
    private BigDecimal openPrice;

    /**
     * Giá đóng lệnh
     */
    private BigDecimal closePrice;

    /**
     * Thời điểm mở lệnh
     */
    private Instant openTime;

    /**
     * Thời điểm đóng lệnh
     */
    private Instant closeTime;

    /**
     * Lợi nhuận của chính lệnh này
     */
    private BigDecimal profit;

    /**
     * OPEN / CLOSED
     */
    private String tradeStatus;

    /**
     * WIN / LOSS
     */
    private String result;


    // =====================================
    // ACCOUNT BALANCE
    // =====================================

    /**
     * Số dư tài khoản tại thời điểm mở lệnh
     */
    private BigDecimal openBalance;

    /**
     * Số dư tài khoản tại thời điểm đóng lệnh
     */
    private BigDecimal closeBalance;


    // =====================================
    // BOT CONFIGURATION
    // =====================================

    /**
     * Phương pháp giao dịch
     * LOW_PROFIT / HIGH_PROFIT
     */
    private String strategy;

    /**
     * Có sử dụng Multi Lot
     */
    private Boolean multiLot;

    /**
     * Chuỗi Lot
     * Ví dụ: 0.01,0.02,0.03
     */
    @Column(length = 1000)
    private String lotChain;


    // =====================================
    // RR
    // =====================================

    private BigDecimal rrRisk;

    private BigDecimal rrReward;


    // =====================================
    // MA
    // =====================================

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


    // =====================================
    // FILTER
    // =====================================

    /**
     * Sideway
     */
    private Boolean sideway;

    /**
     * FOMO
     */
    private Boolean fomo;


    // =====================================
    // SYNC
    // =====================================

    /**
     * PENDING / SYNCED
     */
    private String syncStatus;

    /**
     * Thời điểm đồng bộ thành công
     */
    private Instant syncedDt;


    // =====================================
    // SYSTEM
    // =====================================

    /**
     * Thời điểm tạo record
     */
    private Instant createdDt;

    /**
     * Thời điểm cập nhật record
     */
    private Instant updatedDt;
    private Boolean firstTradeAfterBotStart;
    private String lotNumber;
    private String autoBot;
}