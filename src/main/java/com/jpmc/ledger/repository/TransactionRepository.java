package com.jpmc.ledger.repository;

import com.jpmc.ledger.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Page<Transaction> findByFromAccountIdOrToAccountId(String fromId, String toId, Pageable pageable);
}