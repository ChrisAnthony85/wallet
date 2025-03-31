package com.example.wallet.model.dto;

import java.math.BigDecimal;

public class TransactionDTO {
    private Long id;
    private BigDecimal amount;
    private String currency;
    private String type;
    private Long accountId;  // Include only accountId

    // Constructor
    public TransactionDTO(Long id, BigDecimal amount, String currency, String type, Long accountId) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.accountId = accountId;
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


}

