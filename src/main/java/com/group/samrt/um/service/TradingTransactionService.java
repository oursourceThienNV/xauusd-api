package com.group.samrt.um.service;

import com.group.samrt.um.domain.uml.TradingTransaction;
import com.group.samrt.um.respository.TradingTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class TradingTransactionService {
    private static final String SYNCED = "SYNCED";
    @Autowired
    TradingTransactionRepository tradingTransactionRepository;
    public TradingTransaction save(TradingTransaction transaction) {

        Optional<TradingTransaction> optional =
                tradingTransactionRepository
                        .findByTicket(transaction.getTicket());

        // =========================
        // INSERT
        // =========================
        if (optional.isEmpty()) {

            // Chỉ INSERT mới bắt buộc account
            if (transaction.getAccount() == null
                    || transaction.getAccount().isBlank()) {
                return null;
            }

            transaction.setCreatedDt(Instant.now());
            transaction.setUpdatedDt(Instant.now());
            transaction.setAutoBot("true");

            return tradingTransactionRepository.save(transaction);
        }

        // =========================
        // UPDATE
        // =========================

        TradingTransaction existing = optional.get();

        if (transaction.getClosePrice() != null) {
            existing.setClosePrice(
                    transaction.getClosePrice()
            );
        }
        if (transaction.getAccount() != null) {
            existing.setAccount(
                    transaction.getAccount()
            );
        }

        if (transaction.getCloseTime() != null) {
            existing.setCloseTime(
                    transaction.getCloseTime()
            );
        }

        if (transaction.getProfit() != null) {
            existing.setProfit(
                    transaction.getProfit()
            );
        }

        if (transaction.getTradeStatus() != null) {
            existing.setTradeStatus(
                    transaction.getTradeStatus()
            );
        }

        if (transaction.getResult() != null) {
            existing.setResult(
                    transaction.getResult()
            );
        }

        if (transaction.getCloseBalance() != null) {
            existing.setCloseBalance(
                    transaction.getCloseBalance()
            );
        }

        if (transaction.getSyncStatus() != null) {
            existing.setSyncStatus(
                    transaction.getSyncStatus()
            );
        }

        if (transaction.getSyncedDt() != null) {
            existing.setSyncedDt(
                    transaction.getSyncedDt()
            );
        }

        existing.setUpdatedDt(Instant.now());

        return tradingTransactionRepository.save(existing);
    }
    @Transactional(readOnly = true)
    public TradingTransaction findByTicket(Long ticket) {
        return tradingTransactionRepository
                .findByTicket(ticket)
                .orElse(null);
    }
    @Transactional(readOnly = true)
    public String checkResult(Long ticket) {

        TradingTransaction transaction = tradingTransactionRepository
                .findByTicket(ticket)
                .orElse(null);

        if (transaction == null) {
            log.warn("❌ CHECK RESULT | Ticket={} | Không tìm thấy transaction", ticket);
            return null;
        }

        String lotNumber = transaction.getLotNumber();
        String result = transaction.getResult();

        log.info(
                "🔎 CHECK RESULT | Ticket={} | CurrentLot={} | Result={}",
                ticket,
                lotNumber,
                result
        );

        if (lotNumber == null || result == null) {
            log.warn(
                    "❌ CHECK RESULT | Ticket={} | CurrentLot={} | Result={} | Không đủ dữ liệu",
                    ticket,
                    lotNumber,
                    result
            );
            return null;
        }

        // WIN → L1
        if ("WIN".equalsIgnoreCase(result)) {

            log.info(
                    "🏆 CHECK RESULT | Ticket={} | CurrentLot=L{} | Result=WIN | NextLot=L1",
                    ticket,
                    lotNumber
            );

            return "1";
        }

        // LOSS
        if ("LOSS".equalsIgnoreCase(result)) {

            String nextLotNumber;

            switch (lotNumber) {
                case "1":
                    nextLotNumber = "2";
                    break;

                case "2":
                    nextLotNumber = "3";
                    break;

                case "3":
                    nextLotNumber = "4";
                    break;

                case "4":
                    nextLotNumber = "5";
                    break;

                case "5":
                    nextLotNumber = "1";
                    break;

                default:
                    log.warn(
                            "❌ CHECK RESULT | Ticket={} | CurrentLot={} | Result=LOSS | Lot không hợp lệ",
                            ticket,
                            lotNumber
                    );
                    return null;
            }

            log.info(
                    "📕 CHECK RESULT | Ticket={} | CurrentLot=L{} | Result=LOSS | NextLot=L{}",
                    ticket,
                    lotNumber,
                    nextLotNumber
            );

            return nextLotNumber;
        }

        log.warn(
                "❌ CHECK RESULT | Ticket={} | CurrentLot={} | Result={} | Result không hợp lệ",
                ticket,
                lotNumber,
                result
        );

        return null;
    }
    @Transactional
    public List<TradingTransaction> saveAll(
            List<TradingTransaction> transactions) {

        if (transactions == null || transactions.isEmpty()) {
            return Collections.emptyList();
        }

        /*
         * =====================================================
         * 1. Lấy account
         * =====================================================
         *
         * Mỗi request history của bot nên thuộc về 1 account.
         */
        String account = transactions.stream()
                .filter(Objects::nonNull)
                .map(TradingTransaction::getAccount)
                .filter(accountValue ->
                        accountValue != null
                                && !accountValue.isBlank())
                .findFirst()
                .orElse(null);

        if (account == null) {
            log.warn("TRADING HISTORY SYNC SKIP | Account is null");
            return Collections.emptyList();
        }

        /*
         * =====================================================
         * 2. Lấy toàn bộ ticket từ request
         * =====================================================
         */

        Set<Long> tickets = transactions.stream()
                .filter(Objects::nonNull)
                .map(TradingTransaction::getTicket)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (tickets.isEmpty()) {
            return Collections.emptyList();
        }

        /*
         * =====================================================
         * 3. Query DB 1 lần
         * =====================================================
         */

        List<TradingTransaction> existingList =
                tradingTransactionRepository
                        .findAllByAccountAndTicketIn(
                                account,
                                tickets
                        );

        /*
         * =====================================================
         * 4. Map theo ticket
         * =====================================================
         */

        Map<Long, TradingTransaction> existingMap =
                existingList.stream()
                        .collect(Collectors.toMap(
                                TradingTransaction::getTicket,
                                Function.identity()
                        ));

        List<TradingTransaction> toSave =
                new ArrayList<>();

        Instant now = Instant.now();

        /*
         * =====================================================
         * 5. INSERT / UPDATE
         * =====================================================
         */

        for (TradingTransaction transaction : transactions) {

            if (transaction == null
                    || transaction.getTicket() == null) {
                continue;
            }

            Long ticket = transaction.getTicket();

            TradingTransaction existing =
                    existingMap.get(ticket);

            // =================================================
            // INSERT
            // =================================================

            if (existing == null) {

                transaction.setSyncStatus(SYNCED);
                transaction.setCreatedDt(now);
                transaction.setUpdatedDt(now);

                toSave.add(transaction);

                log.debug(
                        "TRADING HISTORY INSERT | Account={} | Ticket={}",
                        account,
                        ticket
                );

                continue;
            }

            // =================================================
            // Đã SYNCED → bỏ qua
            // =================================================

            if (SYNCED.equals(existing.getSyncStatus())) {

                log.debug(
                        "TRADING HISTORY SKIP | Account={} | Ticket={} | Status=SYNCED",
                        account,
                        ticket
                );

                continue;
            }

            // =================================================
            // UPDATE
            // Chỉ update 3 field
            // =================================================

            existing.setResult(
                    transaction.getResult()
            );

            existing.setProfit(
                    transaction.getProfit()
            );

            existing.setAutoBot(
                    transaction.getAutoBot()
            );

            existing.setSyncStatus(SYNCED);
            existing.setUpdatedDt(now);

            toSave.add(existing);

            log.debug(
                    "TRADING HISTORY UPDATE | Account={} | Ticket={}",
                    account,
                    ticket
            );
        }

        /*
         * =====================================================
         * 6. Batch save
         * =====================================================
         */

        if (!toSave.isEmpty()) {

            return tradingTransactionRepository
                    .saveAll(toSave);
        }

        return Collections.emptyList();
    }
}
