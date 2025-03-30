package com.example.wallet;

import com.example.wallet.model.TransferRequest;
import com.example.wallet.service.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{id}/transfer")
    public void transfer(@PathVariable Long id, @RequestBody TransferRequest request) {
        walletService.requestTransfer(request);
    }
}
