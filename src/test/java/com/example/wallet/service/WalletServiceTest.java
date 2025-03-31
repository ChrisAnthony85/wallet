package com.example.wallet.service;

import com.example.wallet.model.entity.Balance;
import com.example.wallet.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private TransactionProcessor transactionProcessor;

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private WalletService walletService;

    private final Long accountId = 1L;
    private final String currency = "USD";

    @BeforeEach
    void setUp() {
    }

    @Test
    void getBalance_ShouldReturnBalance_WhenAccountExists() {
        // Given
        BigDecimal expectedBalance = new BigDecimal("100.00");
        Balance mockBalance = new Balance();
        mockBalance.setAmount(expectedBalance);

        when(balanceRepository.findByAccountIdAndCurrency(accountId, currency))
                .thenReturn(Optional.of(mockBalance));

        // When
        BigDecimal result = walletService.getBalance(accountId, currency);

        // Then
        assertEquals(expectedBalance, result);
        verify(balanceRepository, times(1)).findByAccountIdAndCurrency(accountId, currency);
    }

    @Test
    void getBalance_ShouldThrowException_WhenAccountNotFound() {
        // Given
        when(balanceRepository.findByAccountIdAndCurrency(accountId, currency))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                walletService.getBalance(accountId, currency)
        );

        assertEquals("Account or currency not found", exception.getMessage());
        verify(balanceRepository, times(1)).findByAccountIdAndCurrency(accountId, currency);
    }
}