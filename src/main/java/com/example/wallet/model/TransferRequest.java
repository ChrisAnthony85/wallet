package com.example.wallet.model;

import java.io.Serializable;
import java.math.BigDecimal;

public record TransferRequest(String transactionId,Long accountId,
                              String owner, BigDecimal amount,
                              String currency, RequestType type)
    implements Serializable
{}
