package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.enums.StorageLocation;
import com.vozhe.jwt.models.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Inventory extends Base {
    private String batchNumber;
    private String meatType;
    private String cut;
    private Double weight;
    private Integer pieces;
    @Enumerated(EnumType.STRING)
    private StorageLocation storageLocation;
    private LocalDate expiryDate;
    private LocalDate receivedDate;
    private Double costPerKg;
    private String status;
}