
package com.vozhe.jwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetrics {
    private double totalReceived;
    private double totalProcessed;
    private double totalIssued;
    private double currentStock;
    private double totalStockChicken;
    private double totalStockBeef;
    private double totalPiecesChicken;
    private double totalPiecesBeef;
    private double averageYield;
    private double wastagePercentage;
    private long pendingRequisitions;
}
