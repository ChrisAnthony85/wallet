package com.example.wallet.exception;

public class AccountOrBalanceNotFoundException extends RuntimeException{
    public AccountOrBalanceNotFoundException(String message) {
        super(message);
    }
}
