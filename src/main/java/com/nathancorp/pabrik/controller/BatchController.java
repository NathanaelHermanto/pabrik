package com.nathancorp.pabrik.controller;

import com.nathancorp.pabrik.dto.request.CreateBatchRequest;
import com.nathancorp.pabrik.dto.request.UpdateBatchRequest;
import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @GetMapping
    public Page<Batch> getBatches(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return batchService.getAllBatch(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Batch> getBatchById(@PathVariable String id) {
        return ResponseEntity.ok(batchService.getBatchById(id));
    }

    @PostMapping
    public ResponseEntity<Batch> createBatch(@RequestBody CreateBatchRequest batchRequest) {
        return ResponseEntity.ok(batchService.createBatch(batchRequest.getPaddyAndQuantity(), batchRequest.getProducedQuantity()));
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Batch> patchBatchStatusById(@PathVariable String id, @RequestBody UpdateBatchRequest batchRequest) {
        return ResponseEntity.ok(batchService.updateBatchStatus(id, batchRequest.isProcessed(), batchRequest.getProducedQuantity()));
    }

}