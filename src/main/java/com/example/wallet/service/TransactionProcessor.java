package com.example.wallet.service;

import com.example.wallet.model.Account;
import com.example.wallet.model.Balance;
import com.example.wallet.model.Transaction;
import com.example.wallet.model.TransferRequest;
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

import java.math.BigDecimal;
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
        String lockKey = "wallet_lock:" + request.accountId() + ":" + request.currency();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                Account account = accountRepository.findById(request.accountId())
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                Balance balance = balanceRepository.findByAccountIdAndCurrency(account.getId(), request.currency())
                        .orElseGet(() -> balanceRepository.save(new Balance(null, BigDecimal.ZERO, request.currency(), account, 0L)));

                if ("DEBIT".equalsIgnoreCase(request.type()) && balance.getAmount().compareTo(request.amount()) < 0) {
                    throw new RuntimeException("Insufficient balance");
                }

                if ("DEBIT".equalsIgnoreCase(request.type())) {
                    balance.setAmount(balance.getAmount().subtract(request.amount()));
                } else if ("CREDIT".equalsIgnoreCase(request.type())) {
                    balance.setAmount(balance.getAmount().add(request.amount()));
                } else {
                    throw new RuntimeException("Invalid transaction type");
                }

                Transaction transaction = new Transaction(null, request.amount(), request.currency(), request.type(), account);
                transactionRepository.save(transaction);
                balanceRepository.save(balance);
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

    @RabbitListener(queues = "transactionQueue.dlq")
    public void processFailedTransaction(TransferRequest request) {
        System.err.println("Failed transaction moved to DLQ: " + request);
    }
}
