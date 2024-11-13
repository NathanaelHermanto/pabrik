package com.nathancorp.pabrik.controller;

import com.nathancorp.pabrik.dto.request.CreatePaddyRequest;
import com.nathancorp.pabrik.model.Paddy;
import com.nathancorp.pabrik.service.PaddyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/paddy")
@RequiredArgsConstructor
public class PaddyController {

    private final PaddyService paddyService;

    @GetMapping
    public ResponseEntity<List<Paddy>> getPaddies() {
        return ResponseEntity.ok(paddyService.getAllPaddies());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Paddy>> getAvailablePaddies() {
        return ResponseEntity.ok(paddyService.getAllAvailablePaddies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paddy> getPaddyById(@PathVariable String id) {
        return ResponseEntity.ok(paddyService.getPaddyById(id));
    }

    @PostMapping
    public ResponseEntity<Paddy> createPaddy(@RequestBody CreatePaddyRequest paddy) {
        return ResponseEntity.ok(paddyService.createPaddy(paddy.getPrice(), paddy.getQuantity(), paddy.getSupplier()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePaddy(@PathVariable String id) {
        paddyService.deletePaddy(UUID.fromString(id));
        return ResponseEntity.ok("Paddy deleted");
    }

}