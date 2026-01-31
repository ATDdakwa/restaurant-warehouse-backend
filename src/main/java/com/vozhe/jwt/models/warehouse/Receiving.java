
package com.vozhe.jwt.models.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.enums.ProcessingStatus;
import com.vozhe.jwt.enums.QualityStatus;
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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"batchNumber"})
})
public class Receiving extends Base {
    private String supplierId;
    private String supplierName;
    @ManyToOne
    @JoinColumn(name = "meat_type_id")  // Use meat_type_id
    private Meat meatType;

    // Transient field to accept string input from frontend
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String meatTypeName;

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

    // Custom setter to handle both String and Object for meatType
    // Handle string input for meatType
    @JsonSetter("meatType")
    public void setMeatTypeAsString(String meatName) {
        this.meatTypeName = meatName;
    }
}
