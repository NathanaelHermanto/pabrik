package com.nathancorp.pabrik.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Paddy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NonNull
    private Double quantity;

    @NonNull
    private Double price;

    private String supplier;

    private Storage storage;

    @NonNull
    private LocalDateTime purchaseDate;

//    @ManyToMany(mappedBy = "paddies")
//    private List<Batch> batches;

    private double processedQuantity;

//    public void setBatches(Batch batch) {
//        this.batches.add(batch);
//    }

}