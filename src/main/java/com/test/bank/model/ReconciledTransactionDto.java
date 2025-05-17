package com.test.bank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReconciledTransactionDto {

    private String primaryId;
    private String secondaryId;
    private String eventType;
    private String stepCode;
    private Integer stepRank;
    private Integer eventRank;
    private String date;
    private Boolean valid;
}

