package com.example.wallet.service;

import com.example.wallet.model.entity.Account;
import com.example.wallet.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void createAccount_shouldSaveAndReturnAccount() {
        // Given
        String owner = "John Testowner";
        Account mockAccount = Account.builder()
                .id(1L)
                .owner(owner)
                .timestamp(Instant.now())
                .build();

        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        // When
        Account createdAccount = accountService.createAccount(owner);

        // Then
        assertNotNull(createdAccount);
        assertEquals(owner, createdAccount.getOwner());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void getAccountById_whenAccountExists_shouldReturnAccount() {
        // Given
        Long accountId = 1L;
        Account mockAccount = new Account();
        mockAccount.setId(accountId);
        mockAccount.setOwner("Jane Doe");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));

        // When
        Optional<Account> account = accountService.getAccountById(accountId);

        // Then
        assertTrue(account.isPresent());
        assertEquals(accountId, account.get().getId());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountById_whenAccountDoesNotExist_shouldReturnEmpty() {
        // Given
        Long accountId = 2L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // When
        Optional<Account> account = accountService.getAccountById(accountId);

        // Then
        assertFalse(account.isPresent());
        verify(accountRepository, times(1)).findById(accountId);
    }
}
