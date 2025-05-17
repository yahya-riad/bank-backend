package com.test.bank.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.bank.entity.TransactionEvent;
import com.test.bank.mapper.TransactionMapper;
import com.test.bank.model.TransactionEventDTO;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final ObjectMapper objectMapper;
    private final TransactionMapper transactionMapper;
    private final EntityManager entityManager;

    @Value("${app.loader.batch-size:1000}")
    private int batchSize;


    private static final Path INPUT_DIR = Paths.get("data/input");
    private static final Path PROCESSED_DIR = Paths.get("data/processed");
    private static final Path ERROR_DIR = Paths.get("data/error");

    private static final String FILE_NAME = "transactions.json";

    @Async
    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            Files.createDirectories(INPUT_DIR);
            Files.createDirectories(PROCESSED_DIR);
            Files.createDirectories(ERROR_DIR);

            Path source = INPUT_DIR.resolve(FILE_NAME);

            if (!Files.exists(source)) {
                log.warn("Fichier JSON non trouvé à {}", source);
                return;
            }

            load(source);
        } catch (IOException e) {
            log.error("Erreur initialisation des répertoires : {}", e.getMessage(), e);
        }
    }

    public void load(Path source) throws IOException {
        try (InputStream input = Files.newInputStream(source)) {

            List<TransactionEventDTO> transactionEventDTOS = objectMapper.readValue(
                    input, new TypeReference<>() {}
            );

            List<TransactionEvent> events = transactionEventDTOS.stream()
                    .map(transactionMapper::toEntity)
                    .peek(this::validate)
                    .toList();

            persistInBatch(events, batchSize);

            log.info("{} transactions traitées (invalides : {})",
                    events.size(),
                    events.stream().filter(transactionEvent -> !transactionEvent.isValid()).count());

            moveFile(source, PROCESSED_DIR.resolve(source.getFileName()));

        } catch (Exception e) {
            log.error("Échec chargement transactions : {}", e.getMessage(), e);
            moveFile(source, ERROR_DIR.resolve(source.getFileName()));
        }
    }

    private void validate(TransactionEvent event) {
        boolean isValid = event.getEventType() != null &&
                !event.getEventType().isBlank() &&
                isValidDate(event.getDate());

        event.setValid(isValid);
    }

    private boolean isValidDate(String date) {
        try {
            LocalDateTime.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void moveFile(Path source, Path target) {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Fichier déplacé vers {}", target);
        } catch (IOException e) {
            log.error("Échec déplacement fichier : {}", e.getMessage(), e);
        }
    }

    private void persistInBatch(List<TransactionEvent> events, int batchSize) {
        for (int i = 0; i < events.size(); i++) {
            entityManager.persist(events.get(i));

            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }
}