
package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.enums.ProcessingStatus;
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
    private String meatType;

    // ADD THESE TWO FIELDS
    private String productType;  // To store the type (e.g., "meats", "1" for Dry Goods, etc.)
    private String productId;    // To store the actual product ID when it's not meat
    private String productName;  // To store the product name for display purposes

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

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status = ProcessingStatus.PENDING;

    private Double processedWeight = 0.0;

    private Integer processedQuantity = 0;

    private String paymentType;
    // retrieved from DB
    private String currency;
}
