package com.nathancorp.pabrik.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Rice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NonNull
    private Double quantity;

    private RiceType riceType;

    private LocalDateTime productionDate;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private Batch batch;

    private Storage storage;

}