package com.example.wallet.model;

import java.math.BigDecimal;

public record TransferRequest(Long accountId, BigDecimal amount, String currency, RequestType type) {}
