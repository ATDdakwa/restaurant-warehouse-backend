package com.vozhe.jwt.controller;

import com.vozhe.jwt.models.DryGoodsStockTakeRecord;
import com.vozhe.jwt.models.StockTakeRecord;
import com.vozhe.jwt.payload.request.DryGoodsStockTakeRequest;
import com.vozhe.jwt.service.ProductInventoryService;
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
    private final ProductInventoryService productInventoryService; // Inject ProductInventoryService

    @PostMapping("stock-take-records")
    public ResponseEntity<List<StockTakeRecord>> submitStockTake(
            @RequestBody List<StockTakeRecord> records) {
        List<StockTakeRecord> savedRecords = stockTakeRecordService.saveStockTakeRecords(records);
        return ResponseEntity.ok(savedRecords);
    }

    @PostMapping("dry-goods-stock-take") // New endpoint for dry goods stock take
    public ResponseEntity<String> submitDryGoodsStockTake(
            @RequestBody List<DryGoodsStockTakeRequest> requests) {
        productInventoryService.processDryGoodsStockTake(requests);
        return ResponseEntity.ok("Dry goods stock take submitted successfully!");
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

    @GetMapping("dry-records-stock-take-records")
    public ResponseEntity<List<DryGoodsStockTakeRecord>> getDryGoodsStockTakeHistory(
            @RequestParam(required = false) String meatType,
            @RequestParam(required = false) String itemType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<DryGoodsStockTakeRecord> history = productInventoryService.getFilteredStockTakeRecords(meatType, itemType, startDate, endDate);
        return ResponseEntity.ok(history);
    }
}
