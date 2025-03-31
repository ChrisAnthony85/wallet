package com.example.wallet.service;

import com.example.wallet.model.entity.Account;
import com.example.wallet.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(String owner) {
        Account newAccount = new Account();
        newAccount.setOwner(owner);
        newAccount.setTimestamp(Instant.now());
        return accountRepository.save(newAccount);
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
}
