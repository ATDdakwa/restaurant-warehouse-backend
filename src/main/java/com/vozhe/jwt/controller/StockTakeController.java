package com.vozhe.jwt.controller;

import com.vozhe.jwt.models.StockTakeRecord;
import com.vozhe.jwt.service.StockTakeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/whitelist/api/")
@CrossOrigin(origins = "http://localhost:3000")
public class StockTakeController {

    private final StockTakeRecordService stockTakeRecordService;

    @PostMapping("stock-take-records")
    public ResponseEntity<List<StockTakeRecord>> submitStockTake(
            @RequestBody List<StockTakeRecord> records) {
        List<StockTakeRecord> savedRecords = stockTakeRecordService.saveStockTakeRecords(records);
        return ResponseEntity.ok(savedRecords);
    }

    @GetMapping("stock-take-records")
    public ResponseEntity<List<StockTakeRecord>> getStockTakeHistory(
            @RequestParam(required = false) String meatType,
            @RequestParam(required = false) String itemType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<StockTakeRecord> history = stockTakeRecordService.getFilteredStockTakeRecords(meatType, itemType, startDate, endDate);
        return ResponseEntity.ok(history);
    }
}
