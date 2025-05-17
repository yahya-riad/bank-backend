package com.test.bank.repository;

import com.test.bank.entity.ReferenceEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceEventRepository extends JpaRepository<ReferenceEvent, String> {
    List<ReferenceEvent> findAllByOrderByEventRankAsc();
}