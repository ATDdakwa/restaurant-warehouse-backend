
package com.vozhe.jwt.controller.warehouse;

import com.vozhe.jwt.enums.DistributionStatus;
import com.vozhe.jwt.exceptions.InvalidInputException;
import com.vozhe.jwt.models.warehouse.*;
import com.vozhe.jwt.payload.request.PaymentDto;
import com.vozhe.jwt.repository.warehouse.DistributionRepository;
import com.vozhe.jwt.repository.warehouse.InventoryRepository;
import com.vozhe.jwt.service.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final InventoryRepository inventoryRepository;
    private final DistributionRepository distributionRepository;

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

    // Update a supplier
    @PutMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> updateSupplier(
            @PathVariable Long id,
            @RequestBody Supplier supplierDetails) {
        Supplier updatedSupplier = warehouseService.updateSupplier(id, supplierDetails);
        return ResponseEntity.ok(updatedSupplier);
    }

    // Delete a supplier
    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        warehouseService.deleteSupplier(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // Receiving endpoints
    @PostMapping("/receiving")
    public ResponseEntity<Receiving> createReceiving(@RequestBody Receiving receiving) {
        return ResponseEntity.ok(warehouseService.saveReceiving(receiving));
    }
    @PutMapping("/receiving/{id}")
    public ResponseEntity<Receiving> updateReceiving(@PathVariable Long id, @RequestBody Receiving receivingDetails) {
        Receiving updateReceivedItem = warehouseService.updateReceiving(id, receivingDetails);
        return ResponseEntity.ok(updateReceivedItem);
    }

    @GetMapping("/receiving")
    public ResponseEntity<List<Receiving>> getAllReceivings() {
        return ResponseEntity.ok(warehouseService.getAllReceivings());
    }

    @GetMapping("/receivingByCredits")
    public ResponseEntity<List<Receiving>> getAllCreditReceiving() {
        return ResponseEntity.ok(warehouseService.getAllCreditReceiving());
    }

    // ReceivingController.java
    @PutMapping("/receiving/{id}/mark-paid")
    public ResponseEntity<Receiving> markReceivingAsPaid(
            @PathVariable Long id,
            @RequestBody PaymentDto paymentDto
    ) {
        Receiving updated = warehouseService.markReceivingAsPaid(id, paymentDto);
        return ResponseEntity.ok(updated);
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

    // Add this temporary endpoint to see current state
    @GetMapping("/inventory/analysis")
    public ResponseEntity<?> analyzeInventory() {
        List<Inventory> all = inventoryRepository.findAll();

        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalEntries", all.size());
        analysis.put("uniqueBatches", all.stream()
                .map(Inventory::getBatchNumber)
                .distinct()
                .count());
        analysis.put("groupedByCut", all.stream()
                .collect(Collectors.groupingBy(
                        inv -> inv.getMeatType().getName() + " - " + inv.getCut(),
                        Collectors.summingDouble(Inventory::getWeight)
                )));

        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/inventory/migrate")
    public ResponseEntity<?> migrateInventory() {
        try {
            warehouseService.consolidateInventory();
            return ResponseEntity.ok("Migration completed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Migration failed: " + e.getMessage());
        }
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
    @PutMapping("/distribution/{id}")
    public ResponseEntity<Distribution> updateDistribution(
            @PathVariable Long id,
            @RequestBody Distribution updatedDistribution
    ) {
        Distribution existing = distributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        // Only allow editing REQUESTED status
        // APPROVED status should use the /approve endpoint instead
        if (existing.getStatus() != DistributionStatus.REQUESTED) {
            throw new InvalidInputException(
                    "Only REQUESTED distributions can be edited. Use the approval endpoint to adjust approved quantities."
            );
        }

        // Preserve metadata
        updatedDistribution.setId(id);
        updatedDistribution.setStatus(existing.getStatus());
        updatedDistribution.setRequestedAt(existing.getRequestedAt());
        updatedDistribution.setApprovedAt(existing.getApprovedAt());
        updatedDistribution.setIssuedAt(existing.getIssuedAt());
        updatedDistribution.setDeliveredAt(existing.getDeliveredAt());
        updatedDistribution.setReceivedAt(existing.getReceivedAt());

        return ResponseEntity.ok(distributionRepository.save(updatedDistribution));
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

    @PutMapping("/distribution/{id}/acknowledge")
    public ResponseEntity<Distribution> acknowledgeReceiptOfDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.acknowledgeDelivery(id));
    }

    /* ================= SHOP ================= */
    @PutMapping("/distribution/{id}/receive")
    public ResponseEntity<Distribution> confirmReceipt(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.confirmReceipt(id));
    }

    @DeleteMapping("/distribution/duplicates/delete")
    public ResponseEntity<Void> deleteDuplicateDistributions() {
        warehouseService.cleanUpDistributions();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/distribution/requested")
    public ResponseEntity<Void> deleteAllRequested() {
        warehouseService.deleteAllRequestedDistributions();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/distribution/deleteAllRequested")
    public ResponseEntity<Void> deleteRequested() {
        warehouseService.deleteByStatus( DistributionStatus.REQUESTED);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reports")
    public ResponseEntity<com.vozhe.jwt.payload.response.ReportsData> getReportsData() {
        return ResponseEntity.ok(warehouseService.getReportsData());
    }
}
