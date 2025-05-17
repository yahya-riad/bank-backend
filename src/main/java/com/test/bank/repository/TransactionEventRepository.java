package com.test.bank.repository;

import com.test.bank.entity.TransactionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionEventRepository extends JpaRepository<TransactionEvent, String> {
    Optional<TransactionEvent> findByPrimaryId(String primaryId);

}

