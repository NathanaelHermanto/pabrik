package com.nathancorp.pabrik.controller;

import com.nathancorp.pabrik.dto.request.CreatePaddyRequest;
import com.nathancorp.pabrik.model.Paddy;
import com.nathancorp.pabrik.service.PaddyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/paddy")
@RequiredArgsConstructor
public class PaddyController {

    private final PaddyService paddyService;

    @GetMapping
    public Page<Paddy> getPaddies(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paddyService.getAllPaddies(pageable);
    }

    @GetMapping("/available")
    public Page<Paddy> getAvailablePaddies(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paddyService.getAllAvailablePaddies(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paddy> getPaddyById(@PathVariable String id) {
        return ResponseEntity.ok(paddyService.getPaddyById(id));
    }

    @PostMapping
    public ResponseEntity<Paddy> createPaddy(@RequestBody CreatePaddyRequest paddy) {
        return ResponseEntity.ok(paddyService.createPaddy(paddy.getQuantity(), paddy.getPrice(), paddy.getSupplier()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePaddy(@PathVariable String id) {
        paddyService.deletePaddy(UUID.fromString(id));
        return ResponseEntity.ok("Paddy " + id + " deleted");
    }

}