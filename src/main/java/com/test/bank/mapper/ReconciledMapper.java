package com.test.bank.mapper;

import com.test.bank.entity.ReferenceEvent;
import com.test.bank.entity.TransactionEvent;
import com.test.bank.model.ReconciledTransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReconciledMapper {

    @Mapping(source = "transactionEvent.primaryId", target = "primaryId")
    @Mapping(source = "transactionEvent.secondaryId", target = "secondaryId")
    @Mapping(source = "transactionEvent.eventType", target = "eventType")
    @Mapping(source = "transactionEvent.date", target = "date")
    @Mapping(source = "transactionEvent.valid", target = "valid")
    @Mapping(source = "referenceEvent.stepCode", target = "stepCode")
    @Mapping(source = "referenceEvent.stepRank", target = "stepRank")
    @Mapping(source = "referenceEvent.eventRank", target = "eventRank")
    ReconciledTransactionDto toDto(TransactionEvent transactionEvent, ReferenceEvent referenceEvent);
}
