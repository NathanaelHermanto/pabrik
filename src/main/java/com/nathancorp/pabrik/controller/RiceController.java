package com.nathancorp.pabrik.controller;

import com.nathancorp.pabrik.dto.request.CreateBatchRequest;
import com.nathancorp.pabrik.dto.request.CreateRiceRequest;
import com.nathancorp.pabrik.dto.request.UpdateBatchRequest;
import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.model.Rice;
import com.nathancorp.pabrik.service.BatchService;
import com.nathancorp.pabrik.service.RiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rice")
@RequiredArgsConstructor
public class RiceController {

    private final RiceService riceService;

    @GetMapping
    public Page<Rice> getRice(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return riceService.getAllRice(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rice> getRiceById(@PathVariable String id) {
        return ResponseEntity.ok(riceService.getRiceById(id));
    }

    @PostMapping
    public ResponseEntity<Rice> postRice(@RequestBody CreateRiceRequest riceRequest) {
        return ResponseEntity.ok(riceService.createRice(riceRequest.getQuantity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRice(@PathVariable String id) {
        riceService.deleteRiceById(id);
        return ResponseEntity.ok("Rice " + id + " deleted");
    }

}