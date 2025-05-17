package com.test.bank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEventDTO {

    @JsonProperty("primary_id")
    private String primaryId;

    @JsonProperty("secondary_id")
    private String secondaryId;
    private EventDTO event;
    private String date;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventDTO {
        private String eventType;
    }
}

