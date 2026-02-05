package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.models.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DryGoodsDistributionItem extends Base {
    private Long productInventoryId;
    private String productId;
    private String productName;
    private Integer requestedQuantity;
    private Integer approvedQuantity;
    private Integer issuedQuantity;
    private String unit;
    private Double cost; // Calculated on ISSUE
}