package com.example.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHanlder {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String,String>> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error" , ex.getMessage()) );
    }

    @ExceptionHandler(AccountOrBalanceNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleAccountBalanceNotFound(AccountOrBalanceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error" , ex.getMessage()) );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> handleInvalidAmount(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error" , ex.getMessage()) );
    }
}
