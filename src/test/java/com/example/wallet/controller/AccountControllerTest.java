package com.example.wallet.controller;

import com.example.wallet.model.TransferRequest;
import com.example.wallet.model.dto.CreateAccountRequest;
import com.example.wallet.model.entity.Account;
import com.example.wallet.service.AccountService;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static com.example.wallet.model.RequestType.CREDIT;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)  // Loads only the AccountController for testing
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvc will handle HTTP requests

    @MockBean
    private WalletService walletService;  // Mocking the WalletService

    @MockBean
    private AccountService accountService;  // Mocking the AccountService

    @MockBean
    private RabbitTemplate rabbitTemplate;  // Mocking RabbitTemplate

    @BeforeEach
    void setUp() {
        // Mocks are initialized by Spring automatically, no need for MockitoAnnotations.openMocks
    }

    @Test
    void createAccount_shouldReturnSuccessMessage() throws Exception {
        // Given
        CreateAccountRequest request = new CreateAccountRequest("John Doe");
        Account newAccount = new Account(1L, "John Doe", Instant.now());
        when(accountService.createAccount(ArgumentMatchers.any())).thenReturn(newAccount);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"owner\": \"John Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Account created"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(newAccount.getId()));
    }

    @Test
    void getAccount_shouldReturnAccountIfFound() throws Exception {
        // Given
        Long accountId = 1L;
        Account account = new Account(accountId, "John Doe", Instant.now());
        when(accountService.getAccountById(accountId)).thenReturn(Optional.of(account));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(accountId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.owner").value("John Doe"));
    }

    @Test
    void getAccount_shouldReturnNotFoundIfAccountDoesNotExist() throws Exception {
        // Given
        Long accountId = 1L;
        when(accountService.getAccountById(accountId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{accountId}", accountId))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Account with ID 1 not found"));
    }

    @Test
    void getBalance_shouldReturnBalanceIfFound() throws Exception {
        // Given
        Long accountId = 1L;
        String currency = "USD";
        BigDecimal balance = new BigDecimal("100.00");
        when(walletService.getBalance(accountId, currency)).thenReturn(balance);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}/balance", accountId)
                        .param("currency", currency))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(balance.doubleValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(accountId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value(currency));
    }

    @Test
    void getBalance_shouldReturnNotFoundIfBalanceNotFound() throws Exception {
        // Given
        Long accountId = 1L;
        String currency = "USD";
        when(walletService.getBalance(accountId, currency)).thenThrow(new RuntimeException("Balance not found"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}/balance", accountId)
                        .param("currency", currency))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Balance not found"));
    }

    @Test
    void transfer_shouldReturnSuccessMessage() throws Exception {
        // Given
        TransferRequest request = new TransferRequest("txn123", 1L, "John Doe", new BigDecimal("100.00"), "USD", CREDIT);
        doNothing().when(rabbitTemplate).convertAndSend("transferExchange", "transfer", request);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionId\": \"txn123\", \"accountId\": 1, \"accountHolder\": \"John Doe\", \"amount\": 100.00, \"currency\": \"USD\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Transfer request submitted"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionId").value("txn123"));
    }

    @Test
    void transfer_shouldReturnBadRequestForInvalidAmount() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionId\": \"txn123\", \"accountId\": 1, \"accountHolder\": \"John Doe\", \"amount\": -100.00, \"currency\": \"USD\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Error").value("Amount must be positive"));
    }
}
