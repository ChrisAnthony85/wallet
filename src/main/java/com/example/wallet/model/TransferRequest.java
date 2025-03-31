package com.example.wallet.model;

import java.io.Serializable;
import java.math.BigDecimal;

public record TransferRequest(String transactionId, Long accountId,
                              String owner, BigDecimal amount,
                              String currency, RequestType type)
    implements Serializable
{
    public TransferRequest {
        // Check if the amount is positive
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
