package com.nathancorp.pabrik.repository;


import com.nathancorp.pabrik.model.Paddy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PaddyRepository extends JpaRepository<Paddy, UUID> {
    @Query("SELECT p FROM Paddy p WHERE p.processedQuantity < p.quantity")
    Page<Paddy> findAvailablePaddies(Pageable pageable);
}

