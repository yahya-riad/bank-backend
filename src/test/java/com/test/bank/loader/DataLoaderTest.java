package com.test.bank.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.bank.entity.TransactionEvent;
import com.test.bank.mapper.TransactionMapper;
import com.test.bank.model.TransactionEventDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.*;

class DataLoaderTest {

    @InjectMocks
    private DataLoader dataLoader;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataLoader = new DataLoader(objectMapper, transactionMapper, entityManager);
    }

    @Test
    void shouldLoadValidTransactionsAndPersistThem() throws Exception {
        // Given
        String json = "[{ \"primaryId\": \"ID1\", \"secondaryId\": \"ID2\", \"event\": { \"eventType\": \"Reception\" }, \"date\": \"2025-05-20T10:00:00\" }]";
        Path tempFile = Files.createTempFile("transactions", ".json");
        Files.write(tempFile, json.getBytes());


        var eventDto = TransactionEventDTO.EventDTO.builder().eventType("Reception").build();
        var dto = TransactionEventDTO.builder()
                .primaryId("ID1").secondaryId("ID2").event(eventDto).date("2025-05-20T10:00:00").build();

        var event = TransactionEvent.builder()
                .primaryId("ID1")
                .secondaryId("ID2")
                .eventType("Reception")
                .date("2025-05-20T10:00:00")
                .build();

        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<TransactionEventDTO>>>any()))
                .thenReturn(List.of(dto));
        when(transactionMapper.toEntity(dto)).thenReturn(event);

        // When
        dataLoader.load(tempFile);

        // Then
        verify(entityManager, atLeastOnce()).persist(any(TransactionEvent.class));
        Files.deleteIfExists(tempFile);
    }
}
