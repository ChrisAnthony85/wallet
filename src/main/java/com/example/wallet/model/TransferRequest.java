package com.example.wallet.model;

import java.math.BigDecimal;

public record TransferRequest(String transactionId,Long accountId, String owner, BigDecimal amount, String currency, RequestType type) {}
