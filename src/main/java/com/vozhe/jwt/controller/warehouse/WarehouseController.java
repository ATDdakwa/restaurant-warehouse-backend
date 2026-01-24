
package com.vozhe.jwt.controller.warehouse;

import com.vozhe.jwt.models.warehouse.*;
import com.vozhe.jwt.service.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    // Supplier endpoints
    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(warehouseService.saveSupplier(supplier));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<com.vozhe.jwt.payload.response.DashboardMetrics> getDashboardMetrics() {
        return ResponseEntity.ok(warehouseService.getDashboardMetrics());
    }

    @GetMapping("/suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(warehouseService.getAllSuppliers());
    }

    // Receiving endpoints
    @PostMapping("/receiving")
    public ResponseEntity<Receiving> createReceiving(@RequestBody Receiving receiving) {
        return ResponseEntity.ok(warehouseService.saveReceiving(receiving));
    }

    @GetMapping("/receiving")
    public ResponseEntity<List<Receiving>> getAllReceivings() {
        return ResponseEntity.ok(warehouseService.getAllReceivings());
    }

    // Inventory endpoints
    @PostMapping("/inventory")
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        return ResponseEntity.ok(warehouseService.saveInventory(inventory));
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<Inventory>> getAllInventories() {
        return ResponseEntity.ok(warehouseService.getAllInventories());
    }

    // Distribution endpoints
    /* ================= SHOP ================= */
    @PostMapping(value = "/distribution", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Distribution> createDistribution(
            @RequestBody Distribution distribution
    ) {
        return ResponseEntity.ok(warehouseService.requestDistribution(distribution));
    }

    /* ================= ALL ROLES ================= */
    @GetMapping("/distribution")
    public ResponseEntity<List<Distribution>> getAllDistributions() {
        return ResponseEntity.ok(warehouseService.getAllDistributions());
    }

    /* ================= ADMIN / STOCK CONTROLLER ================= */
    @PutMapping("/distribution/{id}/approve")
    public ResponseEntity<Distribution> approveDistribution(
            @PathVariable Long id,
            @RequestBody List<DistributionItem> approvedItems
    ) {
        return ResponseEntity.ok(
                warehouseService.approveDistribution(id, approvedItems)
        );
    }

    /* ================= STOCK CONTROLLER ================= */
    @PutMapping("/distribution/{id}/issue")
    public ResponseEntity<Distribution> issueDistribution(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.issueDistribution(id));
    }

    /* ================= DRIVER ================= */
    @PutMapping("/distribution/{id}/deliver")
    public ResponseEntity<Distribution> confirmDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.confirmDelivery(id));
    }

    /* ================= SHOP ================= */
    @PutMapping("/distribution/{id}/receive")
    public ResponseEntity<Distribution> confirmReceipt(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.confirmReceipt(id));
    }

    @GetMapping("/reports")
    public ResponseEntity<com.vozhe.jwt.payload.response.ReportsData> getReportsData() {
        return ResponseEntity.ok(warehouseService.getReportsData());
    }
}
