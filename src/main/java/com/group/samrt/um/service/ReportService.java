package com.group.samrt.um.service;

import com.group.samrt.um.client.client.response.AccountReportResponse;
import com.group.samrt.um.client.client.response.TradeReportResponse;
import com.group.samrt.um.domain.uml.AdminUser;
import com.group.samrt.um.domain.uml.TradingTransaction;
import com.group.samrt.um.respository.AdminUserRepository;
import com.group.samrt.um.respository.TradingTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TradingTransactionRepository tradingTransactionRepository;

    private final AdminUserRepository adminUserRepository;


    // =========================================================
    // 1. DANH SÁCH ACCOUNT BÁO CÁO
    // =========================================================

    @Transactional(readOnly = true)
    public Page<AccountReportResponse> getAccountReports(
            Instant from,
            Instant to,
            String keyword,
            Pageable pageable
    ) {

        Page<String> accountPage =
                tradingTransactionRepository.findReportAccounts(
                        from,
                        to,
                        keyword,
                        pageable
                );

        return accountPage.map(
                account -> buildAccountReport(
                        account,
                        from,
                        to
                )
        );
    }


    // =========================================================
    // BUILD ACCOUNT REPORT
    // =========================================================

    private AccountReportResponse buildAccountReport(
            String account,
            Instant from,
            Instant to
    ) {

        AccountReportResponse response =
                new AccountReportResponse();


        // =====================================================
        // ACCOUNT
        // =====================================================

        response.setAccount(account);


        // =====================================================
        // PROFIT
        // =====================================================

        BigDecimal profit =
                tradingTransactionRepository.sumReportProfit(
                        account,
                        from,
                        to
                );

        response.setProfit(
                profit != null
                        ? profit
                        : BigDecimal.ZERO
        );


        // =====================================================
        // TOTAL TRADES
        // =====================================================

        Long totalTrades =
                tradingTransactionRepository.countReportTrades(
                        account,
                        from,
                        to
                );

        if (totalTrades == null) {
            totalTrades = 0L;
        }

        response.setTotalTrades(totalTrades);


        // =====================================================
        // WIN TRADES
        // =====================================================

        Long winTrades =
                tradingTransactionRepository.countReportWinTrades(
                        account,
                        from,
                        to
                );

        if (winTrades == null) {
            winTrades = 0L;
        }

        response.setWinTrades(winTrades);


        // =====================================================
        // WIN RATE
        // =====================================================

        BigDecimal winRate = BigDecimal.ZERO;

        if (totalTrades > 0) {

            winRate =
                    BigDecimal.valueOf(winTrades)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(
                                    BigDecimal.valueOf(totalTrades),
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        response.setWinRate(winRate);


        // =====================================================
        // CURRENT BALANCE
        // =====================================================

        List<BigDecimal> balances =
                tradingTransactionRepository.findReportLatestBalance(
                        account,
                        PageRequest.of(0, 1)
                );

        BigDecimal balance = BigDecimal.ZERO;

        if (balances != null
                && !balances.isEmpty()
                && balances.get(0) != null) {

            balance = balances.get(0);
        }

        response.setBalance(balance);


        // =====================================================
        // USER / LICENSE
        // =====================================================

        AdminUser user =
                adminUserRepository.findByUsername(account);

        if (user == null) {

            response.setStatus("00");
            response.setLicenseExpiredDt(null);
            response.setRemainingDays(0L);

            return response;
        }


        // =====================================================
        // STATUS
        // =====================================================

        response.setStatus(
                user.getStatus()
        );


        // =====================================================
        // LICENSE
        // =====================================================

        Instant licenseExpiredDt =
                user.getLicenseExpiredDt();

        if (licenseExpiredDt == null) {

            response.setLicenseExpiredDt(null);
            response.setRemainingDays(0L);

        } else {

            response.setLicenseExpiredDt(
                    licenseExpiredDt.toString()
            );

            long remainingDays =
                    ChronoUnit.DAYS.between(
                            Instant.now(),
                            licenseExpiredDt
                    );

            if (remainingDays < 0) {
                remainingDays = 0;
            }

            response.setRemainingDays(
                    remainingDays
            );
        }


        return response;
    }


    // =========================================================
    // 2. CHI TIẾT GIAO DỊCH
    // =========================================================

    @Transactional(readOnly = true)
    public Page<TradeReportResponse> getTradeReports(
            String account,
            Instant from,
            Instant to,
            String keyword,
            Pageable pageable
    ) {

        Page<TradingTransaction> tradePage =
                tradingTransactionRepository.findReportTrades(
                        account,
                        from,
                        to,
                        keyword,
                        pageable
                );

        return tradePage.map(
                this::toTradeReportResponse
        );
    }


    // =========================================================
    // MAP FULL TradingTransaction
    // =========================================================

    private TradeReportResponse toTradeReportResponse(
            TradingTransaction trade
    ) {

        TradeReportResponse response =
                new TradeReportResponse();


        // =====================================================
        // SYSTEM
        // =====================================================

        response.setId(
                trade.getId()
        );

        response.setCreatedDt(
                trade.getCreatedDt()
        );

        response.setUpdatedDt(
                trade.getUpdatedDt()
        );


        // =====================================================
        // ACCOUNT
        // =====================================================

        response.setAccount(
                trade.getAccount()
        );


        // =====================================================
        // TRADE
        // =====================================================

        response.setTicket(
                trade.getTicket()
        );

        response.setType(
                trade.getType()
        );

        response.setLot(
                trade.getLot()
        );

        response.setOpenPrice(
                trade.getOpenPrice()
        );

        response.setClosePrice(
                trade.getClosePrice()
        );

        response.setOpenTime(
                trade.getOpenTime()
        );

        response.setCloseTime(
                trade.getCloseTime()
        );

        response.setProfit(
                trade.getProfit()
        );

        response.setTradeStatus(
                trade.getTradeStatus()
        );

        response.setResult(
                trade.getResult()
        );


        // =====================================================
        // ACCOUNT BALANCE
        // =====================================================

        response.setOpenBalance(
                trade.getOpenBalance()
        );

        response.setCloseBalance(
                trade.getCloseBalance()
        );


        // =====================================================
        // BOT CONFIGURATION
        // =====================================================

        response.setStrategy(
                trade.getStrategy()
        );

        response.setMultiLot(
                trade.getMultiLot()
        );

        response.setLotChain(
                trade.getLotChain()
        );


        // =====================================================
        // RR
        // =====================================================

        response.setRrRisk(
                trade.getRrRisk()
        );

        response.setRrReward(
                trade.getRrReward()
        );


        // =====================================================
        // MA
        // =====================================================

        response.setMaFast(
                trade.getMaFast()
        );

        response.setMaSlow(
                trade.getMaSlow()
        );

        response.setMaTrendValue(
                trade.getMaTrendValue()
        );


        // =====================================================
        // FILTER
        // =====================================================

        response.setSideway(
                trade.getSideway()
        );

        response.setFomo(
                trade.getFomo()
        );


        // =====================================================
        // SYNC
        // =====================================================

        response.setSyncStatus(
                trade.getSyncStatus()
        );

        response.setSyncedDt(
                trade.getSyncedDt()
        );


        // =====================================================
        // BOT START
        // =====================================================

        response.setFirstTradeAfterBotStart(
                trade.getFirstTradeAfterBotStart()
        );


        return response;
    }
}