package com.nathancorp.pabrik.service;

import com.nathancorp.pabrik.exception.InvalidQuantityException;
import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.model.Rice;
import com.nathancorp.pabrik.model.RiceType;
import com.nathancorp.pabrik.model.Storage;
import com.nathancorp.pabrik.repository.RiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiceService {

    private static final Logger LOGGER= LoggerFactory.getLogger(RiceService.class);

    private final RiceRepository repository;

    public Rice createRice(Batch batch) {
        if (batch == null) {
            LOGGER.info("Failed to create Rice, batch must not be null");
            throw new NullPointerException("Batch must not be null");
        }

        Rice pr = Rice.builder()
                .batch(batch)
                .quantity(batch.getProducedQuantity())
                .productionDate(LocalDateTime.now())
                .storage(Storage.STORAGE_3)
                .build();

        pr = repository.save(pr);
        LOGGER.info(String.format("Rice %s is created", pr.getId().toString()));
        return pr;
    }

    public Rice createRice(Double quantity) {
        if (quantity <= 0) {
            LOGGER.info("Failed to create Rice, quantity must be greater than 0");
            throw new InvalidQuantityException("Quantity must be greater than 0");
        }

        Rice r = Rice.builder()
                .quantity(quantity)
                .productionDate(LocalDateTime.now())
                .storage(Storage.STORAGE_3)
                .batch(null)
                .riceType(RiceType.GRADE_A)
                .build();

        r = repository.save(r);
        LOGGER.info(String.format("Rice %s is created", r.getId().toString()));
        return r;
    }

    public void deleteRiceById(String id) {
        if (repository.existsById(UUID.fromString(id))) {
            repository.deleteById(UUID.fromString(id));
            LOGGER.info(String.format("Rice %s is deleted", id));
        } else {
            LOGGER.info(String.format("Failed to delete Rice %s , not found", id));
            throw new EntityNotFoundException("Rice with ID " + id + " not found");
        }
    }

    public List<Rice> getAllRice() {
        return repository.findAll();
    }

    public Rice getRiceById(String id) {
        return repository.findById(UUID.fromString(id)).orElseThrow(()
                -> new EntityNotFoundException("Rice with ID " + id + " not found"));
    }
}
