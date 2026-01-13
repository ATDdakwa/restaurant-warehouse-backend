
package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.enums.QualityStatus;
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
public class Receiving extends Base {
    private String supplierId;
    private String supplierName;
    @Enumerated(EnumType.STRING)
    private MeatType meatType;
    private LocalDate deliveryDate;
    private String batchNumber;
    private Integer quantity;
    private Double totalWeight;
    private Double cost;
    private Double averageWeight;
    @Enumerated(EnumType.STRING)
    private QualityStatus qualityStatus;
    private String qualityNotes;
    private String receivedBy;
}
