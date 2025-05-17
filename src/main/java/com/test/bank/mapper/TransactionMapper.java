package com.test.bank.mapper;

import com.test.bank.entity.TransactionEvent;
import com.test.bank.model.TransactionEventDTO;
import org.springframework.stereotype.Component;


@Component
public class TransactionMapper {
    public TransactionEvent toEntity(TransactionEventDTO dto) {
        return TransactionEvent.builder()
                .primaryId(dto.getPrimaryId())
                .secondaryId(dto.getSecondaryId())
                .date(dto.getDate())
                .eventType(dto.getEvent().getEventType())
                .build();
    }
}