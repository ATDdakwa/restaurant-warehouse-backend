package com.vozhe.jwt.service.reports;

import lombok.Data;

@Data
public class DailyConsumptionReportRow {
    private String date;
    private String outlet;
    private String meatType;
    private String cut;
    private Double totalWeight;
    private Integer totalPieces;
    private Double totalCost;
}
