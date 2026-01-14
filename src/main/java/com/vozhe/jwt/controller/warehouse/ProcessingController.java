
package com.vozhe.jwt.controller.warehouse;

import com.vozhe.jwt.models.warehouse.Processing;
import com.vozhe.jwt.service.warehouse.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/processing")
@RequiredArgsConstructor
public class ProcessingController {

    private final ProcessingService processingService;

    @PostMapping
    public ResponseEntity<Processing> createProcessing(@RequestBody Processing processing) {
        return ResponseEntity.ok(processingService.saveProcessing(processing));
    }

    @GetMapping("/history/{batchNumber}")
    public ResponseEntity<List<Processing>> getProcessingHistory(@PathVariable String batchNumber) {
        return ResponseEntity.ok(processingService.getProcessingHistory(batchNumber));
    }

    @GetMapping
    public ResponseEntity<List<Processing>> getAllProcessings() {
        return ResponseEntity.ok(processingService.getAllProcessings());
    }
}
