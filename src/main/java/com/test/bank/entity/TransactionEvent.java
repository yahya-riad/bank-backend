package com.test.bank.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transaction_event")
public class TransactionEvent {
    @Id
    private String primaryId;
    private String secondaryId;
    private String eventType;
    private String date;
    private boolean valid;

//    public TransactionEvent markAsInvalid() {
//        this.isValid = true;
//        return this;
//    }
//
//    public TransactionEvent markAsValid() {
//        this.isValid = false;
//        return this;
//    }
}

