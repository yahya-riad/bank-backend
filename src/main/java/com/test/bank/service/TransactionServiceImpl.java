package com.test.bank.service;

import com.test.bank.entity.TransactionEvent;
import com.test.bank.mapper.ReconciledMapper;
import com.test.bank.model.ReconciledTransactionDto;
import com.test.bank.repository.ReferenceEventRepository;
import com.test.bank.repository.TransactionEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionEventRepository transactionRepository;
    private final ReferenceEventRepository referenceEventRepository;
    private final ReconciledMapper reconciledMapper;

    @Override
    public List<ReconciledTransactionDto> reconcileTransactionEvent(String startingPrimaryId) {
        Set<String> ids = new HashSet<>();
        List<TransactionEvent> transactionEvents = new ArrayList<>();
        var referenceEvents = referenceEventRepository.findAllByOrderByEventRankAsc();

        var currentId = startingPrimaryId;
        while (currentId != null && ids.add(currentId)) {
            var transactionEvent = transactionRepository.findByPrimaryId(currentId);
            if (transactionEvent.isPresent()) {
                transactionEvents.add(transactionEvent.get());
                currentId = transactionEvent.get().getSecondaryId();
            } else {
                log.warn("Transaction non trouvée pour id={}", currentId);
                break;
            }
        }

        transactionEvents.sort(Comparator.comparing(TransactionEvent::getDate));

        Map<String, Integer> eventTypeCounter = new HashMap<>();

        return transactionEvents.stream()
                .map(transaction -> {
                    String type = transaction.getEventType();
                    int occurrence = eventTypeCounter.getOrDefault(type, 0);
                    eventTypeCounter.put(type, occurrence + 1);

                    var referenceEvent = referenceEvents.stream()
                            .filter(r -> r.getEventType().equals(type))
                            .skip(occurrence)
                            .findFirst()
                            .orElse(null);

                    if (referenceEvent == null) {
                        log.warn("Référence non trouvée pour eventType='{}' (occurrence #{})", type, occurrence + 1);
                    }

                    return reconciledMapper.toDto(transaction, referenceEvent);
                })
                .toList();
    }

}
