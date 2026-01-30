package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.StorageLocation;
import com.vozhe.jwt.models.Base;
import com.vozhe.jwt.models.Meat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Inventory extends Base {
    private String batchNumber;
    @ManyToOne
    @JoinColumn(name = "meat_type_id")
    private Meat meatType;
    private String cut;
    private Double weight;
    private Integer pieces;
    @Enumerated(EnumType.STRING)
    private StorageLocation storageLocation;
    private LocalDate expiryDate;
    private LocalDate receivedDate;
    private Double costPerKg;
    private String status;
    @Column(name = "source_batches")
    private String sourceBatches; // "BATCH001,BATCH002,BATCH003"
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    @PrePersist
    protected void onCreate() {
        if (receivedDate == null) {
            receivedDate = LocalDate.now();
        }
    }
}