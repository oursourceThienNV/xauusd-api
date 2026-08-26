package com.group.samrt.um.service;

import com.group.samrt.um.domain.uml.TradingTransaction;
import com.group.samrt.um.respository.TradingTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional(rollbackFor = {Exception.class})
public class TradingTransactionService {
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

            transaction.setCreatedDt(Instant.now());
            transaction.setUpdatedDt(Instant.now());

            return tradingTransactionRepository.save(transaction);
        }

        // =========================
        // UPDATE
        // =========================

        TradingTransaction existing = optional.get();

        // Chỉ update những field thực sự được gửi lên

        if (transaction.getClosePrice() != null) {
            existing.setClosePrice(
                    transaction.getClosePrice()
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

        existing.setUpdatedDt(
                Instant.now()
        );

        return tradingTransactionRepository.save(existing);
    }

}
