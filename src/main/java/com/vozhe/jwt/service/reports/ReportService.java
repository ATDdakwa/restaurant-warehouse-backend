package com.vozhe.jwt.service.reports;

import com.vozhe.jwt.models.warehouse.Distribution;
import com.vozhe.jwt.models.warehouse.DistributionItem;
import com.vozhe.jwt.repository.warehouse.DistributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final DistributionRepository distributionRepository;

    public List<DailyConsumptionReportRow> getDailyConsumptionReport(LocalDate startDate, LocalDate endDate) {
        List<Distribution> distributions = distributionRepository.findByIssuedDateBetween(startDate, endDate);

        Map<String, DailyConsumptionReportRow> aggregatedData = new HashMap<>();

        for (Distribution distribution : distributions) {
            for (DistributionItem item : distribution.getItems()) {
                String key = distribution.getIssuedDate().toString() + "-" + distribution.getOutlet() + "-" + item.getCut();

                aggregatedData.computeIfAbsent(key, k -> {
                    DailyConsumptionReportRow row = new DailyConsumptionReportRow();
                    row.setDate(distribution.getIssuedDate().toString());
                    row.setOutlet(distribution.getOutlet());
                    row.setMeatType(item.getMeatType().toString());
                    row.setCut(item.getCut());
                    row.setTotalWeight(0.0);
                    row.setTotalPieces(0);
                    row.setTotalCost(0.0);
                    return row;
                });

                DailyConsumptionReportRow row = aggregatedData.get(key);
                row.setTotalWeight(row.getTotalWeight() + (item.getIssuedWeight() != null ? item.getIssuedWeight() : 0.0));
                row.setTotalPieces(row.getTotalPieces() + (item.getRequestedPieces() != null ? item.getRequestedPieces() : 0));
                row.setTotalCost(row.getTotalCost() + (item.getCost() != null ? item.getCost() : 0.0));
            }
        }

        return new ArrayList<>(aggregatedData.values());
    }
}
