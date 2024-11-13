package com.nathancorp.pabrik.service;

import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.model.Paddy;
import com.nathancorp.pabrik.model.Rice;
import com.nathancorp.pabrik.model.Storage;
import com.nathancorp.pabrik.repository.BatchRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    public Batch createBatch(Map<String, Double> paddyAndQuantity, Double producedQuantity) {
        List<Paddy> paddies = new ArrayList<>();

        // Check if each paddy has enough quantity
        for (Map.Entry<String, Double> entry : paddyAndQuantity.entrySet()) {
            String paddyId = entry.getKey();
            Double quantity = entry.getValue();

            if (quantity <= 0) {
                logger.error("Failed to create Batch, invalid quantity, should be greater than 0");
                throw new IllegalArgumentException("Invalid quantity");
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
                .build();

        batch = batchRepository.save(batch);
        logger.info(String.format("Batch %s is created", batch.getId().toString()));

        Rice rice = riceService.createRice(batch);
        logger.info(String.format("Rice %s is created", rice.getId().toString()));

        paddyService.updatePaddiesProcessedQuantity(paddyAndQuantity, batch);

        return batch;
    }

    public List<Batch> getAllBatch() {
        return batchRepository.findAll();
    }

    public Batch getBatchById(String id) {
        return batchRepository.findById(UUID.fromString(id)).orElseThrow(()
                -> new EntityNotFoundException("Batch with ID " + id + " not found"));
    }
}
