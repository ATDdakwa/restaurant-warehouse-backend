package com.vozhe.jwt.controller.reports;

import com.vozhe.jwt.service.reports.DailyConsumptionReportRow;
import com.vozhe.jwt.service.reports.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/warehouse/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily-consumption")
    public ResponseEntity<List<DailyConsumptionReportRow>> getDailyConsumptionReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<DailyConsumptionReportRow> report = reportService.getDailyConsumptionReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }
}
