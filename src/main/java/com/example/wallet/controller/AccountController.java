package com.example.wallet.controller;

import com.example.wallet.model.dto.TransactionDTO;
import com.example.wallet.model.entity.Account;
import com.example.wallet.model.dto.CreateAccountRequest;
import com.example.wallet.model.TransferRequest;
import com.example.wallet.model.entity.Transaction;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.service.AccountService;
import com.example.wallet.service.WalletService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
class AccountController {
    private final WalletService walletService;
    private final AccountService accountService;
    private final RabbitTemplate rabbitTemplate;
    private final TransactionRepository transactionRepository;

    public AccountController(WalletService walletService, AccountService accountService,
                             RabbitTemplate rabbitTemplate, TransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.accountService = accountService;
        this.rabbitTemplate = rabbitTemplate;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest createAccountRequest) {
        Account newAccount = accountService.createAccount(createAccountRequest.owner());
        return ResponseEntity.ok(Map.of("message", "Account created", "accountId", newAccount.getId()));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Object> getAccount(@PathVariable Long accountId) {
        return accountService.getAccountById(accountId)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)  // Explicit type declaration
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Account with ID " + accountId + " not found")));
    }

//    @GetMapping("/{id}/balance")
//    public ResponseEntity<?> getBalance(@PathVariable Long id, @RequestParam String currency) {
//        try {
//            BigDecimal balance = walletService.getBalance(id, currency);
//            return ResponseEntity.ok(Map.of("accountId", id, "currency", currency, "balance", balance));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
//        }
//    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalances(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(walletService.getBalances(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        rabbitTemplate.convertAndSend("transferExchange", "transfer", request);
        return ResponseEntity.ok(Map.of("message", "Transfer request submitted",
                "transactionId", request.transactionId()));
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable Long accountId) {
        List<Transaction> transactions = accountService.getTransactionsByAccount(accountId);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(t -> new TransactionDTO(
                        t.getId(),
                        t.getAmount(),
                        t.getCurrency(),
                        t.getType().name(),
                        t.getAccount().getId()
                )).toList();
        return ResponseEntity.ok(transactionDTOs);
    }

}
