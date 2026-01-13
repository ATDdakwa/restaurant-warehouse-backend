
package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.BeefCut;
import com.vozhe.jwt.enums.ChickenCut;
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
    @Enumerated(EnumType.STRING)
    private MeatType meatType;
    @Enumerated(EnumType.STRING)
    private ChickenCut chickenCut;
    @Enumerated(EnumType.STRING)
    private BeefCut beefCut;
    private Double weight;
    @Enumerated(EnumType.STRING)
    private StorageLocation storageLocation;
    private LocalDate expiryDate;
    private LocalDate receivedDate;
    private Double costPerKg;
    private String status;
}
