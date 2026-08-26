package com.group.samrt.um.respository;

import com.group.samrt.um.domain.uml.TradingTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TradingTransactionRepository
        extends JpaRepository<TradingTransaction, Long>,
        JpaSpecificationExecutor<TradingTransaction> {

    // =========================================================
    // BASIC
    // =========================================================

    Optional<TradingTransaction> findByTicket(Long ticket);


    // =========================================================
    // ACCOUNT LIST
    // Dùng cho màn hình quản lý tài khoản
    //
    // Không quan tâm account có giao dịch hay không
    // vì nguồn dữ liệu ở đây chính là TradingTransaction.
    // =========================================================

    @Query("""
        SELECT t.account
        FROM TradingTransaction t
        WHERE t.account IS NOT NULL
          AND t.account <> ''
          AND (
              :keyword IS NULL
              OR :keyword = ''
              OR LOWER(t.account)
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        GROUP BY t.account
        ORDER BY t.account
    """)
    Page<String> findAccounts(
            @Param("keyword") String keyword,
            Pageable pageable
    );


    // =========================================================
    // CURRENT BALANCE
    //
    // Lấy closeBalance mới nhất của account
    // =========================================================

    @Query("""
        SELECT t.closeBalance
        FROM TradingTransaction t
        WHERE t.account = :account
          AND t.closeBalance IS NOT NULL
        ORDER BY t.closeTime DESC
    """)
    List<BigDecimal> findLatestBalance(
            @Param("account") String account,
            Pageable pageable
    );


    // =========================================================
    // TOTAL PROFIT
    //
    // Dùng cho thống kê tổng thể account
    // =========================================================

    @Query("""
        SELECT COALESCE(
            SUM(COALESCE(t.profit, 0)),
            0
        )
        FROM TradingTransaction t
        WHERE t.account = :account
    """)
    BigDecimal findTotalProfit(
            @Param("account") String account
    );


    // =========================================================
    // TOTAL TRADES
    //
    // Dùng cho thống kê tổng thể account
    // =========================================================

    @Query("""
        SELECT COUNT(t)
        FROM TradingTransaction t
        WHERE t.account = :account
    """)
    Long countTrades(
            @Param("account") String account
    );


    // =========================================================
    // TOTAL WIN TRADES
    //
    // Dùng cho thống kê tổng thể account
    // =========================================================

    @Query("""
        SELECT COUNT(t)
        FROM TradingTransaction t
        WHERE t.account = :account
          AND t.result = 'WIN'
    """)
    Long countWinTrades(
            @Param("account") String account
    );


    // =========================================================
    // =========================================================
    // REPORT
    // =========================================================
    // =========================================================


    // =========================================================
    // REPORT 1
    // DANH SÁCH ACCOUNT CÓ GIAO DỊCH
    //
    // Chỉ account có giao dịch trong khoảng thời gian
    // from -> to mới được đưa vào báo cáo.
    //
    // Ví dụ:
    //
    // 10001 có giao dịch
    // 10002 không có giao dịch
    // 10003 có giao dịch
    //
    // Kết quả:
    // 10001
    // 10003
    // =========================================================

    @Query("""
        SELECT t.account
        FROM TradingTransaction t
        WHERE t.account IS NOT NULL
          AND t.account <> ''
          AND t.closeTime >= :from
          AND t.closeTime < :to
          AND (
              :keyword IS NULL
              OR :keyword = ''
              OR LOWER(t.account)
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        GROUP BY t.account
        ORDER BY t.account ASC
    """)
    Page<String> findReportAccounts(
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    // =========================================================
    // REPORT 2
    // PROFIT TRONG KHOẢNG THỜI GIAN
    //
    // Profit = SUM(profit)
    // =========================================================

    @Query("""
        SELECT COALESCE(
            SUM(COALESCE(t.profit, 0)),
            0
        )
        FROM TradingTransaction t
        WHERE t.account = :account
          AND t.closeTime >= :from
          AND t.closeTime < :to
    """)
    BigDecimal sumReportProfit(
            @Param("account") String account,
            @Param("from") Instant from,
            @Param("to") Instant to
    );


    // =========================================================
    // REPORT 3
    // TỔNG SỐ GIAO DỊCH TRONG KHOẢNG THỜI GIAN
    // =========================================================

    @Query("""
        SELECT COUNT(t)
        FROM TradingTransaction t
        WHERE t.account = :account
          AND t.closeTime >= :from
          AND t.closeTime < :to
    """)
    Long countReportTrades(
            @Param("account") String account,
            @Param("from") Instant from,
            @Param("to") Instant to
    );


    // =========================================================
    // REPORT 4
    // TỔNG GIAO DỊCH WIN
    //
    // Dùng profit > 0 để thống nhất với cách tính Win Rate
    // =========================================================

    @Query("""
        SELECT COUNT(t)
        FROM TradingTransaction t
        WHERE t.account = :account
          AND t.closeTime >= :from
          AND t.closeTime < :to
          AND t.profit > 0
    """)
    Long countReportWinTrades(
            @Param("account") String account,
            @Param("from") Instant from,
            @Param("to") Instant to
    );


    // =========================================================
    // REPORT 5
    // BALANCE HIỆN TẠI
    //
    // Lấy closeBalance của giao dịch mới nhất.
    //
    // Service sẽ truyền PageRequest.of(0, 1)
    // =========================================================

    @Query("""
        SELECT t.closeBalance
        FROM TradingTransaction t
        WHERE t.account = :account
          AND t.closeBalance IS NOT NULL
        ORDER BY t.closeTime DESC
    """)
    List<BigDecimal> findReportLatestBalance(
            @Param("account") String account,
            Pageable pageable
    );


    // =========================================================
    // REPORT 6
    // CHI TIẾT GIAO DỊCH
    //
    // Lấy toàn bộ TradingTransaction.
    //
    // Filter:
    // - account
    // - from
    // - to
    // - keyword
    //
    // Keyword hỗ trợ:
    // - ticket
    // - type
    // - result
    // - tradeStatus
    //
    // Pagination thực hiện trực tiếp ở DB.
    // =========================================================

    @Query("""
        SELECT t
        FROM TradingTransaction t
        WHERE t.account = :account
          AND t.closeTime >= :from
          AND t.closeTime < :to
          AND (
              :keyword IS NULL
              OR :keyword = ''
              OR LOWER(t.type)
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(t.result)
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(t.tradeStatus)
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(CAST(t.ticket AS string))
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY t.closeTime DESC
    """)
    Page<TradingTransaction> findReportTrades(
            @Param("account") String account,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    // =========================================================
// DASHBOARD
// =========================================================

    /**
     * Tổng số giao dịch
     */
    @Query("""
    SELECT COUNT(t)
    FROM TradingTransaction t
""")
    Long countAllTrades();


    /**
     * Tổng lợi nhuận
     */
    @Query("""
    SELECT COALESCE(SUM(t.profit), 0)
    FROM TradingTransaction t
""")
    BigDecimal sumAllProfit();


    /**
     * Cấu hình có tổng lợi nhuận cao nhất
     *
     * Group theo toàn bộ configuration của bot.
     */
    @Query("""
    SELECT
        t.strategy,
        t.multiLot,
        t.maFast,
        t.maSlow,
        t.maTrendValue,
        t.sideway,
        t.fomo,
        COUNT(t),
        COALESCE(SUM(t.profit), 0),
        COALESCE(
            SUM(
                CASE
                    WHEN t.profit > 0 THEN 1
                    ELSE 0
                END
            ),
            0
        )
    FROM TradingTransaction t
    GROUP BY
        t.strategy,
        t.multiLot,
        t.maFast,
        t.maSlow,
        t.maTrendValue,
        t.sideway,
        t.fomo
    ORDER BY
        COALESCE(SUM(t.profit), 0) DESC
""")
    List<Object[]> findBestConfiguration(
            Pageable pageable
    );
    @Query("""
    SELECT DISTINCT t.account
    FROM TradingTransaction t
    WHERE t.account IS NOT NULL
      AND t.account <> ''
""")
    List<String> findDistinctAccounts();
}