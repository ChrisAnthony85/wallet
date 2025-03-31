package com.example.wallet.repository;

import com.example.wallet.model.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByAccountIdAndCurrency(Long accountId, String currency);
    List<Balance> findAllByAccountId(Long accountId);
}
