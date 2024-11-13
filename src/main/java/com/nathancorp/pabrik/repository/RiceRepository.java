package com.nathancorp.pabrik.repository;


import com.nathancorp.pabrik.model.Rice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RiceRepository extends JpaRepository<Rice, UUID> {
}

