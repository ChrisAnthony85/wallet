package com.example.wallet.model.dto;

import java.math.BigDecimal;

public record TransactionResponse(Long accountId, String currency, BigDecimal balance, String message) {
}
