package com.example.wallet;

import com.example.wallet.model.Account;
import com.example.wallet.model.TransferRequest;
import com.example.wallet.service.AccountService;
import com.example.wallet.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
class AccountController {
    private final WalletService walletService;
    private final AccountService accountService;

    public AccountController(WalletService walletService, AccountService accountService) {
        this.walletService = walletService;
        this.accountService = accountService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount() {
        Account newAccount = accountService.createAccount();
        return ResponseEntity.ok(Map.of("message", "Account created", "accountId", newAccount.getId()));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long id, @RequestParam String currency) {
        try {
            BigDecimal balance = walletService.getBalance(id, currency);
            return ResponseEntity.ok(Map.of("accountId", id, "currency", currency, "balance", balance));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/transfer")
    public ResponseEntity<?> transfer(@PathVariable Long id, @RequestBody TransferRequest request) {
        walletService.requestTransfer(request);
        return ResponseEntity.ok(Map.of("message", "Transfer request processed"));
    }
}
