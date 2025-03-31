package com.example.wallet.service;

import com.example.wallet.model.entity.Balance;
import com.example.wallet.model.TransferRequest;
import com.example.wallet.repository.BalanceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {
    private final TransactionProcessor transactionProcessor;
    private final BalanceRepository balanceRepository;

    public WalletService(TransactionProcessor transactionProcessor, BalanceRepository balanceRepository) {
        this.transactionProcessor = transactionProcessor;
        this.balanceRepository = balanceRepository;
    }

    public BigDecimal getBalance(Long accountId, String currency) {
        return balanceRepository.findByAccountIdAndCurrency(accountId, currency)
                .map(Balance::getAmount)
                .orElseThrow(() -> new RuntimeException("Account or currency not found"));
    }

    public List<Balance> getBalances(Long accountId) {
        return balanceRepository.findAllByAccountId(accountId);
    }
}
