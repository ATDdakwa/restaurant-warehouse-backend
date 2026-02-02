
package com.vozhe.jwt.models.warehouse;

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
public class ProductInventory extends Base {

    private String productId;
    private String productName;
    private Integer quantity;
    private String unit;
    private Double price;
    private LocalDate receivingDate;
    private String batchNumber;
    private String supplierId;
    private String supplierName;

    @Enumerated(EnumType.STRING)
    private StorageLocation storageLocation;
    private LocalDate expiryDate;
    private String status;

}
