package com.test.bank.service;

import com.test.bank.model.ReconciledTransactionDto;
import com.test.bank.entity.ReferenceEvent;
import com.test.bank.entity.TransactionEvent;
import com.test.bank.mapper.ReconciledMapper;
import com.test.bank.repository.ReferenceEventRepository;
import com.test.bank.repository.TransactionEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionEventRepository transactionRepository;

    @Mock
    private ReferenceEventRepository referenceEventRepository;

    @Mock
    private ReconciledMapper reconciledMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReconcileChain_correctly() {
        var transactionEvent1 = TransactionEvent.builder()
                .primaryId("ID1")
                .secondaryId("ID2")
                .eventType("Reception")
                .date(String.valueOf(LocalDateTime.of(2025, 5, 20, 10, 0)))
                .build();

        var transactionEvent2 = TransactionEvent.builder()
                .primaryId("ID2")
                .secondaryId(null)
                .eventType("Duplicate")
                .date(String.valueOf(LocalDateTime.of(2025, 5, 20, 11, 0)))
                .build();


        var referenceEvent1 = ReferenceEvent.builder().eventType("Reception").eventRank(1).stepRank(2).stepCode("Acquisition").build();
        var referenceEvent2 = ReferenceEvent.builder().eventType("Duplicate").eventRank(1).stepRank(2).stepCode("Acquisition").build();

        var dto1 = ReconciledTransactionDto.builder()
                .primaryId("ID1").eventType("Reception").stepCode("Acquisition").eventRank(1).build();
        var dto2 = ReconciledTransactionDto.builder()
                .primaryId("ID2").eventType("Duplicate").stepCode("Acquisition").eventRank(2).build();

        when(transactionRepository.findByPrimaryId("ID1")).thenReturn(Optional.of(transactionEvent1));
        when(transactionRepository.findByPrimaryId("ID2")).thenReturn(Optional.of(transactionEvent2));
        when(referenceEventRepository.findAllByOrderByEventRankAsc()).thenReturn(List.of(referenceEvent1, referenceEvent2));
        when(reconciledMapper.toDto(transactionEvent1, referenceEvent1)).thenReturn(dto1);
        when(reconciledMapper.toDto(transactionEvent2, referenceEvent2)).thenReturn(dto2);

        // Act
        List<ReconciledTransactionDto> result = transactionService.reconcileTransactionEvent("ID1");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(dto1);
        assertThat(result.get(1)).isEqualTo(dto2);

        verify(transactionRepository, times(2)).findByPrimaryId(anyString());
        verify(referenceEventRepository, times(1)).findAllByOrderByEventRankAsc();
    }
}

