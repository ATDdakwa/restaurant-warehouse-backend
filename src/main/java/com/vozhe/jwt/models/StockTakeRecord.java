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
@Table(name = "stock_take_records")
public class StockTakeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemId; // ID of InventoryItem or ReceivingRecord
    private String itemType; // "PROCESSED" or "UNPROCESSED"

    private double systemWeight;
    private double actualWeight;
    private double weightVariance;

    private int systemPieces; // For processed, or quantity for unprocessed
    private int actualPieces; // For processed, or quantity for unprocessed
    private int piecesVariance; // For processed, or quantity for unprocessed

    private LocalDateTime timestamp; // When the stock take was recorded

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
