package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.StorageLocation;
import com.vozhe.jwt.models.Base;
import com.vozhe.jwt.models.Meat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

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

    @PrePersist
    protected void onCreate() {
        if (receivedDate == null) {
            receivedDate = LocalDate.now();
        }
    }
}