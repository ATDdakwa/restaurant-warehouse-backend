
package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.models.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DistributionItem extends Base {
    private Long inventoryId;
    @Enumerated(EnumType.STRING)
    private MeatType meatType;
    private String cut;
    private Double requestedWeight;
    private Double issuedWeight;
    private String batchNumber;
}
