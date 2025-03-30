package com.example.wallet.service;

import com.example.wallet.model.Balance;
import com.example.wallet.model.TransferRequest;
import com.example.wallet.repository.BalanceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {
    private final TransactionProcessor transactionProcessor;
    private final BalanceRepository balanceRepository;

    public WalletService(TransactionProcessor transactionProcessor, BalanceRepository balanceRepository) {
        this.transactionProcessor = transactionProcessor;
        this.balanceRepository = balanceRepository;
    }

    public void requestTransfer(TransferRequest transferRequest) {
        transactionProcessor.processTransaction(transferRequest);
    }

    public BigDecimal getBalance(Long accountId, String currency) {
        return balanceRepository.findByAccountIdAndCurrency(accountId, currency)
                .map(Balance::getAmount)
                .orElseThrow(() -> new RuntimeException("Account or currency not found"));
    }
}
