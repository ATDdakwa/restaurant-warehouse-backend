package com.vozhe.jwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DryGoodsStockTakeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemId; // ID of InventoryItem
    private String productName; // ID of InventoryItem

    private Integer systemQuantity;
    private Integer actualQuantity;
    private Integer quantityVariance;

    private LocalDateTime timestamp; // When the stock take was recorded

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}

