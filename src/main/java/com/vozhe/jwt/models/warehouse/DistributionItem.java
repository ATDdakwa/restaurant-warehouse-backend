
package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.models.Base;
import com.vozhe.jwt.models.Meat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DistributionItem extends Base {
    private Long inventoryId;
    @ManyToOne
    @JoinColumn(name = "meat_type_id")
    private Meat meatType;
    private String cut;

    private Integer requestedPieces;   // SHOP

    private Double approvedWeight;      // STOCK_CONTROLLER

    private Double issuedWeight;        // STOCK_CONTROLLER

    private String batchNumber;

    private Double cost;                // Calculated on ISSUE
}
