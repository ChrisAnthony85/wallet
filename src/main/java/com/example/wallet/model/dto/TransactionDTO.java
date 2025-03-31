package com.example.wallet.model.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

public class TransactionDTO {
    private Long id;
    private BigDecimal amount;
    private String currency;
    private String type;
    private Long accountId;  // Include only accountId
    private Instant timestamp;
    private String status;
    private String remarks;
    private String transactionId;

    // Constructor
    public TransactionDTO(Long id, BigDecimal amount, String currency, String type, Long accountId,
                          Instant timestamp, String status, String remarks, String transactionId) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.accountId = accountId;
        this.timestamp = timestamp;
        this.status = status;
        this.remarks = remarks;
        this.transactionId = transactionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}

