package com.nathancorp.pabrik.repository;


import com.nathancorp.pabrik.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {
}

