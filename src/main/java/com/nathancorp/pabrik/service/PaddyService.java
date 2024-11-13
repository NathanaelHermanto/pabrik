package com.nathancorp.pabrik.service;

import com.nathancorp.pabrik.exception.NegativePriceException;
import com.nathancorp.pabrik.exception.NegativeQuantityException;
import com.nathancorp.pabrik.exception.PaddyNotAvailableForProcessingException;
import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.model.Paddy;
import com.nathancorp.pabrik.model.Storage;
import com.nathancorp.pabrik.repository.PaddyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaddyService {

    private static final Logger logger = LoggerFactory.getLogger(PaddyRepository.class);

    private final PaddyRepository paddyRepository;

    public Paddy createPaddy(Double quantity, Double price, String supplier) {
        if (quantity <= 0) {
            logger.error("Failed to create Paddy, invalid quantity, should be greater than 0");
            throw new NegativeQuantityException("Invalid quantity, should be greater than 0");
        }

        if (price <= 0) {
            logger.error("Failed to create Paddy, invalid price, should be greater than 0");
            throw new NegativePriceException("Invalid price, should be greater than 0");
        }

        Paddy rp = Paddy.builder()
                .price(price)
                .quantity(quantity)
                .supplier(supplier)
                .storage(Storage.STORAGE_1)
                .purchaseDate(LocalDateTime.now())
                .processedQuantity(0)
                .batches(List.of())
                .build();

        rp = paddyRepository.save(rp);
        logger.info(String.format("Paddy %s is created", rp.getId().toString()));
        return rp;
    }

    public void deletePaddy(UUID id) {
        if (paddyRepository.existsById(id)) {
            paddyRepository.deleteById(id);
            logger.info("Paddy {} is deleted", id);
        } else {
            logger.info("Failed to delete Paddy {} , not found", id);
            throw new EntityNotFoundException("Paddy with ID " + id + " not found");
        }
    }

    public Paddy getPaddyById(String id) {
        return paddyRepository.findById(UUID.fromString(id)).orElseThrow(() -> {
            logger.error("Paddy with ID {} not found", id);
            return new EntityNotFoundException("Paddy not found with id " + id);
        });
    }

    public List<Paddy> getAllAvailablePaddies() {
        return paddyRepository.findAll().stream()
                .filter(paddy -> paddy.getProcessedQuantity() < paddy.getQuantity())
                .toList();
    }

    public List<Paddy> getAllPaddies() {
        return paddyRepository.findAll();
    }

    public Paddy getAvailablePaddyByIdForBatch(String id, Double quantity) {
        return paddyRepository.findById(UUID.fromString(id)).stream()
                .filter(paddy -> paddy.getQuantity() >= paddy.getProcessedQuantity() + quantity)
                .findFirst().orElseThrow(() -> {
                    logger.error("Paddy with ID {} not found / not available for processing", id);
                    return new PaddyNotAvailableForProcessingException("Paddy not found / not available for processing with id " + id);
                });
    }

    public void updatePaddiesProcessedQuantity(Map<String, Double> paddyAndQuantity, Batch batch) {
        paddyAndQuantity.forEach((paddyId, quantity) -> {
            paddyRepository.findById(UUID.fromString(paddyId)).ifPresent(paddy -> {
                paddy.setProcessedQuantity(paddy.getProcessedQuantity() + quantity);
                paddy.setBatches(batch);
                paddyRepository.save(paddy);
                logger.info("Updated processed quantity for Paddy with ID: {} to {}", paddyId, paddy.getProcessedQuantity());
            });
        });
    }

}
