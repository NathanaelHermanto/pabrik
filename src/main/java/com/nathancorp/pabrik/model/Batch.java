package com.nathancorp.pabrik.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NonNull
    private Double quantity;

    private Storage storage;

    @NonNull
    private LocalDateTime processingDate;

    private boolean isProcessed;

    @ManyToMany
    @JoinTable(
            name = "batch_paddy",
            joinColumns = @JoinColumn(name = "batch_id"),
            inverseJoinColumns = @JoinColumn(name = "paddy_id"))
    private List<Paddy> paddies;

    @OneToMany(mappedBy = "batch")
    private List<Rice> rice;

    private Double producedQuantity;

    @ElementCollection
    @MapKeyColumn(name = "paddy")
    @Column(name = "paddy_quantity")
    private Map<String, Double> paddiesAndQuantity;
}