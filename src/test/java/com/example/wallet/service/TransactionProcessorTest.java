package com.example.wallet.service;

import com.example.wallet.exception.InsufficientBalanceException;
import com.example.wallet.model.RequestType;
import com.example.wallet.model.TransferRequest;
import com.example.wallet.model.entity.Account;
import com.example.wallet.model.entity.Balance;
import com.example.wallet.model.entity.Transaction;
import com.example.wallet.repository.AccountRepository;
import com.example.wallet.repository.BalanceRepository;
import com.example.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.wallet.model.RequestType.CREDIT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionProcessorTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @Mock
    private RBucket rBucket;

    @InjectMocks
    private TransactionProcessor transactionProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
    }

    @Test
    void processTransaction_shouldProcessCreditTransaction() throws InterruptedException {
        // Given
        String transactionId = UUID.randomUUID().toString();
        TransferRequest request = new TransferRequest(transactionId, 1L, "John Doe", BigDecimal.valueOf(100), "USD", CREDIT);

        Account mockAccount = new Account(1L, "John Doe", Instant.now());
        Balance mockBalance = new Balance(1L, BigDecimal.ZERO, "USD", mockAccount, 0L);
        Transaction mockTransaction = new Transaction(null, BigDecimal.valueOf(100), "USD", CREDIT,
                1L, transactionId, Instant.now(), "SUCCESS", "Transfer Successful");

        when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
        when(balanceRepository.findByAccountIdAndCurrency(1L, "USD")).thenReturn(Optional.of(mockBalance));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);
        when(redissonClient.getBucket(transactionId).isExists()).thenReturn(false);

        // When
        transactionProcessor.processTransaction(request);

        // Then
        assertEquals(BigDecimal.valueOf(100), mockBalance.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(balanceRepository, times(1)).save(any(Balance.class));
    }

    @Test
    void processTransaction_shouldIgnoreDuplicateTransaction() throws InterruptedException {
        // Given
        String transactionId = UUID.randomUUID().toString();
        TransferRequest request = new TransferRequest(transactionId, 1L, "John Doe", BigDecimal.valueOf(100), "USD", CREDIT);

        when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
        when(redissonClient.getBucket(transactionId).isExists()).thenReturn(true);  // Already processed

        // When
        transactionProcessor.processTransaction(request);

        // Then
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(balanceRepository, never()).save(any(Balance.class));
    }

    @Test
    void processTransaction_shouldHandleLockFailure() throws InterruptedException {
        // Given
        String transactionId = UUID.randomUUID().toString();
        TransferRequest request = new TransferRequest(transactionId, 1L, "John Doe", BigDecimal.valueOf(100), "USD", CREDIT);

        when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(false);  // Lock not acquired

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionProcessor.processTransaction(request));
        assertEquals("Could not acquire lock", exception.getMessage());

        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
