package com.example.wallet.model;

import java.math.BigDecimal;

public record TransactionResponse(Long accountId, String currency, BigDecimal balance) {
}
