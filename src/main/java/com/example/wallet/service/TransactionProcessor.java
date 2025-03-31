package com.example.wallet.service;

import com.example.wallet.exception.InsufficientBalanceException;
import com.example.wallet.model.*;
import com.example.wallet.model.dto.TransactionResponse;
import com.example.wallet.model.entity.Account;
import com.example.wallet.model.entity.Balance;
import com.example.wallet.model.entity.Transaction;
import com.example.wallet.repository.AccountRepository;
import com.example.wallet.repository.BalanceRepository;
import com.example.wallet.repository.TransactionRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionProcessor {
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final RedissonClient redissonClient;

    public TransactionProcessor(AccountRepository accountRepository, BalanceRepository balanceRepository,
                                TransactionRepository transactionRepository, RedissonClient redissonClient) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
        this.redissonClient = redissonClient;
    }

    @RabbitListener(queues = "transactionQueue")
    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 3)
    public void processTransaction(TransferRequest request) {
        String lockKey = "wallet_lock:" + request.currency();
        RLock lock = redissonClient.getLock(lockKey);

        String transactionKey = request.transactionId();

        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                // ❌ Check if the transaction is already processed
                if (Boolean.TRUE.equals(redissonClient.getBucket(transactionKey).isExists())) {
                    System.out.println("Duplicate transaction ignored: " + request.transactionId());
                    return ;
                }

                Account account;

                if (request.accountId() == null && request.owner() != null) {
                    Account newAccount = new Account();
                    newAccount.setOwner(request.owner());
                    newAccount.setTimestamp(Instant.now());
                    account = accountRepository.save(newAccount);
                } else {
                    account = accountRepository.findById(request.accountId())
                            .orElseThrow(() -> new RuntimeException("Account does not exist. Create an account first."));
                }

                Balance balance = balanceRepository.findByAccountIdAndCurrency(account.getId(), request.currency())
                        .orElseGet(() -> balanceRepository.save(new Balance(null, BigDecimal.ZERO, request.currency(), account, 0L)));

                switch (request.type()) {
                    case DEBIT -> {
                        if (balance.getAmount().compareTo(request.amount()) < 0) {
                            throw new InsufficientBalanceException("Insufficient balance");
                        }
                        balance.setAmount(balance.getAmount().subtract(request.amount()));
                    }
                    case CREDIT -> balance.setAmount(balance.getAmount().add(request.amount()));
                }

                Transaction transaction = new Transaction(null, request.amount(), request.currency(), request.type(), account, transactionKey, Instant.now());
                transactionRepository.save(transaction);
                balanceRepository.save(balance);

                // ✅ Mark transaction as processed in Redis
                redissonClient.getBucket(transactionKey).set("PROCESSED", 24, TimeUnit.HOURS);
            } else {
                throw new RuntimeException("Could not acquire lock");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Lock acquisition interrupted", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}

