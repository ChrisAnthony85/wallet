package com.example.wallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount = BigDecimal.ZERO;

    private String currency;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Version
    private Long version;
}
