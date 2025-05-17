package com.test.bank.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reference_event")
public class ReferenceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", length = 100)
    private String eventType;

    @Column(name = "step_code", length = 50)
    private String stepCode;

    private int stepRank;
    private int eventRank;
}
