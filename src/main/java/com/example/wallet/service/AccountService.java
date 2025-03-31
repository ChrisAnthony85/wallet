package com.example.wallet.service;

import com.example.wallet.model.entity.Account;
import com.example.wallet.model.entity.Transaction;
import com.example.wallet.repository.AccountRepository;
import com.example.wallet.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createAccount(String owner) {
        Account newAccount = new Account(null, owner, Instant.now());
        return accountRepository.save(newAccount);
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findAllByAccountId(accountId);
    }
}
