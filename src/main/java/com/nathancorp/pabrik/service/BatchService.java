package com.nathancorp.pabrik.service;

import com.nathancorp.pabrik.exception.InvalidQuantityException;
import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.model.Paddy;
import com.nathancorp.pabrik.model.Rice;
import com.nathancorp.pabrik.model.Storage;
import com.nathancorp.pabrik.repository.BatchRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BatchService {

    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);

    private final BatchRepository batchRepository;

    private final PaddyService paddyService;

    private final RiceService riceService;

    /**
     * Takes a map of paddyId and quantity and produced quantity to create a batch
     * Batch can be processed or unprocessed based on the produced quantity
     * if produced quantity is 0, then the Batch.isProcessed is false
     *
     * @param paddyAndQuantity Map<String, Double> paddyId and quantity
     * @param producedQuantity Double produced quantity
     * @return Batch entity
     */
    public Batch createBatch(Map<String, Double> paddyAndQuantity, Double producedQuantity) {
        if (producedQuantity <= 0) {
            logger.error("Failed to create Batch, invalid produced quantity, should be greater than 0");
            throw new InvalidQuantityException("Invalid produced quantity");
        }

        if (paddyAndQuantity.isEmpty()) {
            logger.error("Failed to create Batch, paddy and quantity is empty");
            throw new IllegalArgumentException("Paddy and quantity is empty");
        }

        List<Paddy> paddies = new ArrayList<>();

        // Check if each paddy has enough quantity
        for (Map.Entry<String, Double> entry : paddyAndQuantity.entrySet()) {
            String paddyId = entry.getKey();
            Double quantity = entry.getValue();

            if (quantity <= 0) {
                logger.error("Failed to create Batch, invalid quantity, should be greater than 0");
                throw new InvalidQuantityException("Invalid quantity");
            }

            // Retrieve paddy from database and check quantity
            Paddy paddy = paddyService.getAvailablePaddyByIdForBatch(paddyId,quantity);
            paddies.add(paddy);
        }


        Batch batch = Batch.builder()
                .producedQuantity(producedQuantity)
                .processingDate(LocalDateTime.now())
                .isProcessed(true)
                .storage(Storage.STORAGE_2)
                .paddies(paddies)
                .paddiesAndQuantity(paddyAndQuantity)
                .quantity(paddyAndQuantity.values().stream().mapToDouble(Double::doubleValue).sum())
                .build();

        batch = batchRepository.save(batch);
        logger.info(String.format("Batch %s is created", batch.getId().toString()));

        Rice rice = riceService.createRice(batch);
        logger.info(String.format("Rice %s is created", rice.getId().toString()));

        paddyService.updatePaddiesProcessedQuantity(paddyAndQuantity, batch);

        return batch;
    }

    public Page<Batch> getAllBatch(Pageable pageable) {
        return batchRepository.findAll(pageable);
    }

    public Batch getBatchById(String id) {
        return batchRepository.findById(UUID.fromString(id)).orElseThrow(()
                -> new EntityNotFoundException("Batch with ID " + id + " not found"));
    }

    /**
     * Update batch produced quantity/isProcessed field
     * @param batchId
     * @param isProcessed
     * @param producedQuantity
     * @return updated Batch entity
     */
    public Batch updateBatchStatus(String batchId, boolean isProcessed, Double producedQuantity) {
        if (producedQuantity <= 0) {
            logger.error("Failed to update Batch, invalid produced quantity, should be greater than 0");
            throw new InvalidQuantityException("Invalid produced quantity, should be greater than 0");
        }
        Batch batch = batchRepository.findById(UUID.fromString(batchId)).orElseThrow(
                () -> new EntityNotFoundException("Batch not found"));

        batch.setProcessed(isProcessed);
        batch.setProducedQuantity(producedQuantity);

        batch = batchRepository.save(batch);

        logger.info(String.format("Batch %s updated with processed status: %s and produced quantity: %f",
                batch.getId(), isProcessed, batch.getProducedQuantity()));

        return batch;
    }
}
