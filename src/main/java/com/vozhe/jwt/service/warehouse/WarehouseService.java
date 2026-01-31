
package com.vozhe.jwt.service.warehouse;

import com.vozhe.jwt.enums.DistributionStatus;
import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.enums.ProcessingStatus;
import com.vozhe.jwt.exceptions.InvalidInputException;
import com.vozhe.jwt.models.Meat;
import com.vozhe.jwt.models.settings.GlobalSettings;
import com.vozhe.jwt.models.settings.PaymentType;
import com.vozhe.jwt.models.warehouse.*;
import com.vozhe.jwt.payload.request.PaymentDto;
import com.vozhe.jwt.repository.MeatRepository;
import com.vozhe.jwt.repository.warehouse.*;
import com.vozhe.jwt.service.settings.GlobalSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final SupplierRepository supplierRepository;
    private final ReceivingRepository receivingRepository;
    private final InventoryRepository inventoryRepository;
    private final DistributionRepository distributionRepository;
    private final ProcessingRepository processingRepository;
    private final GlobalSettingsService globalSettingsService;
    private final MeatRepository meatRepository;


    // Supplier actions
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier updateSupplier(Long id, Supplier supplierDetails) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id " + id));

        existing.setName(supplierDetails.getName());
        existing.setContact(supplierDetails.getContact());
        existing.setEmail(supplierDetails.getEmail());
        existing.setAddress(supplierDetails.getAddress());

        return supplierRepository.save(existing);
    }

    public void deleteSupplier(Long id) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id " + id));
        supplierRepository.delete(existing);
    }

    // Receiving actions
    public Receiving saveReceiving(Receiving receiving) {

        // Convert string to Meat entity
        if (receiving.getMeatTypeName() != null) {
            Meat meat = meatRepository.findByName(receiving.getMeatTypeName())
                    .orElseThrow(() -> new RuntimeException("Meat type not found: " + receiving.getMeatTypeName()));
            receiving.setMeatType(meat);
        }

        // Business logic for receiving
        if (receiving.getSupplierId() == null) {
            throw new InvalidInputException("Supplier ID is required");
        }
        supplierRepository.findById(Long.parseLong(receiving.getSupplierId()))
                .orElseThrow(() -> new InvalidInputException("Supplier not found"));

        if (receiving.getQuantity() <= 0 || receiving.getTotalWeight() <= 0) {
            throw new InvalidInputException("Quantity and total weight must be positive");
        }
        receiving.setAverageWeight(receiving.getTotalWeight() / receiving.getQuantity());
        return receivingRepository.save(receiving);
    }

    public Receiving updateReceiving(Long id, Receiving receiving) {
        // Find existing record
        Receiving existing = receivingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receiving record not found with id " + id));

        // Validate that only PENDING records can be edited
        if (existing.getStatus() != ProcessingStatus.PENDING) {
            throw new InvalidInputException("Only PENDING records can be edited");
        }

        // Business logic validations
        if (receiving.getSupplierId() == null) {
            throw new InvalidInputException("Supplier ID is required");
        }

        supplierRepository.findById(Long.parseLong(receiving.getSupplierId()))
                .orElseThrow(() -> new InvalidInputException("Supplier not found"));

        if (receiving.getQuantity() <= 0 || receiving.getTotalWeight() <= 0) {
            throw new InvalidInputException("Quantity and total weight must be positive");
        }

        // Convert meatTypeName string to Meat entity if provided
        if (receiving.getMeatTypeName() != null && !receiving.getMeatTypeName().isEmpty()) {
            Meat meat = meatRepository.findByName(receiving.getMeatTypeName())
                    .orElseThrow(() -> new RuntimeException("Meat type not found: " + receiving.getMeatTypeName()));
            existing.setMeatType(meat);
        } else {
            existing.setMeatType(null); // Clear meat type if not provided
        }

        // Update basic fields
        existing.setSupplierId(receiving.getSupplierId());
        existing.setSupplierName(receiving.getSupplierName());

        // Update product fields
        existing.setProductType(receiving.getProductType());
        existing.setProductId(receiving.getProductId());
        existing.setProductName(receiving.getProductName());

        // Update quantity, weight, and cost
        existing.setQuantity(receiving.getQuantity());
        existing.setTotalWeight(receiving.getTotalWeight());
        existing.setCost(receiving.getCost());

        // Recalculate average weight
        existing.setAverageWeight(receiving.getTotalWeight() / receiving.getQuantity());

        // Update payment information
        existing.setPaymentType(receiving.getPaymentType());
        existing.setCurrency(receiving.getCurrency());

        // Update quality information
        existing.setQualityStatus(receiving.getQualityStatus());
        existing.setQualityNotes(receiving.getQualityNotes());

        // Note: deliveryDate, batchNumber, and receivedBy typically shouldn't change
        // but will include them if the client want them editable:
        // existing.setDeliveryDate(receiving.getDeliveryDate());
        // existing.setBatchNumber(receiving.getBatchNumber());
        // existing.setReceivedBy(receiving.getReceivedBy());

        return receivingRepository.save(existing);
    }

    public List<Receiving> getAllReceivings() {
        return receivingRepository.findAllWithMeatType();
    }

    public List<Receiving> getAllCreditReceiving() {
        return receivingRepository.findByPaymentType("CREDIT");
    }

    // WarehouseService.java
    public Receiving markReceivingAsPaid(Long id, PaymentDto paymentDto) {
        Receiving receiving = receivingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receiving record not found"));

        receiving.setPaymentType(paymentDto.getPaymentType()); // e.g., "Cash"
        receiving.setCurrency(paymentDto.getCurrency());
        receiving.setCost(paymentDto.getCost());

        return receivingRepository.save(receiving);
    }

    // Inventory actions
    public Inventory saveInventory(Inventory inventory) {
        // Business logic for inventory
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    // Distribution actions
    public Distribution saveDistribution(Distribution distribution) {
        // Business logic for distribution
        if (distribution.getItems() == null || distribution.getItems().isEmpty()) {
            throw new InvalidInputException("Distribution items are required");
        }

        double totalApprovedWeight = 0;
        for (DistributionItem item : distribution.getItems()) {
            inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new InvalidInputException("Inventory item not found"));

            item.setApprovedWeight(0.0); // Will be set during approval
            item.setIssuedWeight(0.0); // Will be set during approval
            totalApprovedWeight += item.getApprovedWeight();
        }

        distribution.setTotalWeight(totalApprovedWeight);
        return distributionRepository.save(distribution);
    }

    @Transactional
    public List<Distribution> getAllDistributions() {
        List<Distribution> distributions = distributionRepository.findAll();
        distributions.forEach(distribution -> distribution.getItems().size()); // Initialize items
        return distributions;
    }

    public Distribution requestDistribution(Distribution distribution) {

        // Business logic for distribution
        if (distribution.getItems() == null || distribution.getItems().isEmpty()) {
            throw new InvalidInputException("Distribution items are required");
        }

        double totalApprovedWeight = 0.0;
        for (DistributionItem item : distribution.getItems()) {
            Inventory inventory = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new InvalidInputException("Inventory item not found"));

            if (inventory.getPieces() == null || inventory.getPieces() < item.getRequestedPieces()) {
                throw new InvalidInputException("Not enough pieces for item " + inventory.getId());
            }

            item.setApprovedWeight(0.0); // Will be set during approval
            item.setIssuedWeight(0.0); // Will be set during approval
            item.setApprovedPieces(0);
            totalApprovedWeight += item.getApprovedWeight();
        }


        distribution.setStatus(DistributionStatus.REQUESTED);
        distribution.setRequestedAt(LocalDateTime.now());
        distribution.setTotalWeight(totalApprovedWeight);

        return distributionRepository.save(distribution);
    }

    public Distribution approveDistribution(Long id, List<DistributionItem> approvedItems) {

        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.REQUESTED) {
            throw new InvalidInputException("Only REQUESTED distributions can be approved");
        }

        for (DistributionItem approved : approvedItems) {
            Inventory inventory = inventoryRepository.findById(approved.getInventoryId())
                    .orElseThrow(() -> new InvalidInputException("Inventory item not found")); //removed
            DistributionItem existing = distribution.getItems().stream()
                    .filter(i -> i.getId().equals(approved.getId()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidInputException("Item not found"));

            inventory.setPieces(inventory.getPieces() - approved.getRequestedPieces()); ////removed
            inventoryRepository.save(inventory); //to be removed
            existing.setApprovedWeight(approved.getApprovedWeight());
            existing.setApprovedPieces(approved.getApprovedPieces());
        }

        distribution.setStatus(DistributionStatus.APPROVED);
        distribution.setApprovedAt(LocalDateTime.now());

        return distributionRepository.save(distribution);
    }

    @Transactional
    public Distribution issueDistribution(Long id) {

        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.APPROVED) {
            throw new InvalidInputException("Only APPROVED distributions can be issued");
        }

        double totalIssuedWeight = 0;

        for (DistributionItem item : distribution.getItems()) {

            // Find inventory by meatType and cut instead of inventoryId
            Inventory inventory = inventoryRepository.findByMeatTypeAndCut(
                    item.getMeatType(),
                    item.getCut()
            ).orElseThrow(() -> new InvalidInputException(
                    "Inventory not found for " + item.getMeatType().getName() + " - " + item.getCut()
            ));

            // Validate stock availability
            if (inventory.getWeight() < item.getApprovedWeight()) {
                throw new InvalidInputException("Insufficient stock for " + item.getCut());
            }

//            int availablePieces = inventory.getPieces() != null ? inventory.getPieces() : 0;
//            if (availablePieces < item.getApprovedPieces()) {
//                throw new InvalidInputException("Insufficient pieces for " + item.getCut());
//            }

            // Deduct from inventory
            inventory.setWeight(inventory.getWeight() - item.getApprovedWeight());
//            inventory.setPieces(availablePieces - item.getApprovedPieces()); //to be activated
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);

            // Update the inventoryId to the new consolidated inventory ID
            item.setInventoryId(inventory.getId());

            // Calculate cost
            double costPerKg = globalSettingsService.getCostPerKg(item.getMeatType().getId());
            item.setIssuedWeight(item.getApprovedWeight());
            item.setCost(item.getIssuedWeight() * costPerKg);

            totalIssuedWeight += item.getIssuedWeight();
        }

        distribution.setTotalWeight(totalIssuedWeight);
        distribution.setIssuedAt(LocalDateTime.now());
        distribution.setStatus(DistributionStatus.ISSUED);

        return distributionRepository.save(distribution);
    }

    public Distribution acknowledgeDelivery(Long id) {

        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.ISSUED) {
            throw new InvalidInputException("Only ISSUED distributions can be delivered");
        }

        distribution.setStatus(DistributionStatus.ACKNOWLEDGED);
        distribution.setDeliveredAt(LocalDateTime.now());

        return distributionRepository.save(distribution);
    }

    public Distribution confirmDelivery(Long id) {

        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.ACKNOWLEDGED) {
            throw new InvalidInputException("Only ACKNOWLEDGED distributions can be delivered");
        }

        distribution.setStatus(DistributionStatus.DELIVERED);
        distribution.setDeliveredAt(LocalDateTime.now());

        return distributionRepository.save(distribution);
    }

    public Distribution confirmReceipt(Long id) {

        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.DELIVERED) {
            throw new InvalidInputException("Only DELIVERED distributions can be received");
        }

        distribution.setStatus(DistributionStatus.RECEIVED);
        distribution.setReceivedAt(LocalDateTime.now());

        return distributionRepository.save(distribution);
    }

    public com.vozhe.jwt.payload.response.DashboardMetrics getDashboardMetrics() {
        double totalReceived = receivingRepository.findAll().stream().mapToDouble(Receiving::getTotalWeight).sum();
        double totalProcessed = processingRepository.findAll().stream().mapToDouble(Processing::getTotalOutputWeight).sum();
        double totalIssued = distributionRepository.findAll().stream()
                .filter(d -> "completed".equals(d.getStatus()))
                .mapToDouble(Distribution::getTotalWeight).sum();
        
        List<Inventory> availableInventory = inventoryRepository.findAll().stream()
                .filter(i -> "available".equals(i.getStatus()))
                .toList();

        double currentStock = availableInventory.stream().mapToDouble(Inventory::getWeight).sum();
        double totalStockChicken = availableInventory.stream()
                .filter(i -> Objects.equals(i.getMeatType(), "CHICKEN"))
                .mapToDouble(Inventory::getWeight).sum();
        double totalStockBeef = availableInventory.stream()
                .filter(i -> Objects.equals(i.getMeatType(), "BEEF"))
                .mapToDouble(Inventory::getWeight).sum();

        double totalPiecesChicken = availableInventory.stream()
                .filter(i -> Objects.equals(i.getMeatType(), "CHICKEN") && i.getPieces() != null)
                .mapToInt(Inventory::getPieces).sum();
        double totalPiecesBeef = availableInventory.stream()
                .filter(i -> Objects.equals(i.getMeatType(), "BEEF") && i.getPieces() != null)
                .mapToInt(Inventory::getPieces).sum();

        double averageYield = processingRepository.findAll().stream().mapToDouble(Processing::getYieldPercentage).average().orElse(0);
        double totalWastage = processingRepository.findAll().stream().mapToDouble(Processing::getWastageWeight).sum();
        double wastagePercentage = totalReceived > 0 ? (totalWastage / totalReceived) * 100 : 0;
        long pendingRequisitions = distributionRepository.findAll().stream().filter(d -> "pending".equals(d.getStatus())).count();

        return new com.vozhe.jwt.payload.response.DashboardMetrics(
                totalReceived,
                totalProcessed,
                totalIssued,
                currentStock,
                totalStockChicken,
                totalStockBeef,
                totalPiecesChicken,
                totalPiecesBeef,
                averageYield,
                wastagePercentage,
                pendingRequisitions
        );
    }

    public com.vozhe.jwt.payload.response.ReportsData getReportsData() {
        List<Receiving> receivingRecords = receivingRepository.findAll();
        List<Processing> processingRecords = processingRepository.findAll();
        List<Distribution> distributionRecords = distributionRepository.findAll();
        List<Inventory> inventory = inventoryRepository.findAll();

        return new com.vozhe.jwt.payload.response.ReportsData(
                receivingRecords,
                processingRecords,
                distributionRecords,
                inventory
        );
    }

    @Transactional
    public void consolidateInventory() {
        List<Inventory> allInventory = inventoryRepository.findAll();
        int totalConsolidated = 0;
        int totalMigrated = 0;

        Map<String, List<Inventory>> grouped = allInventory.stream()
                .collect(Collectors.groupingBy(inv ->
                        inv.getMeatType().getId() + "_" + inv.getCut()
                ));

        for (Map.Entry<String, List<Inventory>> entry : grouped.entrySet()) {
            List<Inventory> items = entry.getValue();

            if (items.size() > 1) {
                Inventory consolidated = items.get(0);
                consolidated.setBatchNumber(null);

                double totalWeight = items.stream()
                        .mapToDouble(Inventory::getWeight)
                        .sum();
                int totalPieces = items.stream()
                        .mapToInt(inv -> inv.getPieces() != null ? inv.getPieces() : 0)
                        .sum();

                consolidated.setWeight(totalWeight);
                consolidated.setPieces(totalPieces);

                LocalDate earliestExpiry = items.stream()
                        .map(Inventory::getExpiryDate)
                        .filter(Objects::nonNull)
                        .min(LocalDate::compareTo)
                        .orElse(LocalDate.now().plusDays(5));
                consolidated.setExpiryDate(earliestExpiry);

                String consolidatedSourceBatches = items.stream()
                        .map(Inventory::getBatchNumber)
                        .filter(batch -> batch != null && !batch.isEmpty())
                        .distinct()
                        .collect(Collectors.joining(","));

                consolidated.setSourceBatches(
                        consolidatedSourceBatches.isEmpty() ? null : consolidatedSourceBatches
                );
                consolidated.setLastUpdated(LocalDateTime.now());

                inventoryRepository.save(consolidated);

                // Delete the rest
                for (int i = 1; i < items.size(); i++) {
                    inventoryRepository.delete(items.get(i));
                }

                totalConsolidated += items.size();
                System.out.println("Consolidated " + items.size() + " entries for " +
                        consolidated.getMeatType().getName() + " - " + consolidated.getCut());

            } else if (items.size() == 1) {
                Inventory single = items.get(0);

                if (single.getBatchNumber() != null && !single.getBatchNumber().isEmpty()) {
                    single.setSourceBatches(single.getBatchNumber());
                    single.setBatchNumber(null);
                    single.setLastUpdated(LocalDateTime.now());
                    inventoryRepository.save(single);
                    totalMigrated++;
                }
            }
        }

        System.out.println("Migration complete: " + totalConsolidated +
                " entries consolidated, " + totalMigrated + " entries migrated");
    }

    @Transactional
    public void cleanUpDistributions() {
        List<Distribution> distributions =
                distributionRepository.findByStatusIn(
                        List.of(DistributionStatus.REQUESTED, DistributionStatus.ISSUED)
                );

        Map<String, List<Distribution>> grouped =
                distributions.stream()
                        .collect(Collectors.groupingBy(
                                d -> d.getRequisitionNumber() + "-" + d.getOutlet()
                        ));

        for (List<Distribution> group : grouped.values()) {

            boolean hasIssued = group.stream()
                    .anyMatch(d -> d.getStatus() == DistributionStatus.ISSUED);

            if (hasIssued) {
                // delete all REQUESTED if ISSUED exists
                group.stream()
                        .filter(d -> d.getStatus() == DistributionStatus.REQUESTED)
                        .forEach(distributionRepository::delete);
            } else {
                // optional: remove duplicate REQUESTED, keep one
                List<Distribution> requested = group.stream()
                        .filter(d -> d.getStatus() == DistributionStatus.REQUESTED)
                        .toList();

                for (int i = 1; i < requested.size(); i++) {
                    distributionRepository.delete(requested.get(i));
                }
            }
        }
    }

    @Transactional
    public void deleteAllRequestedDistributions() {
        distributionRepository.deleteByStatus(DistributionStatus.REQUESTED);
    }

    @Transactional
    public void deleteByStatus(DistributionStatus status) {
        List<Distribution> distributions = distributionRepository.findByStatus(status);
        distributionRepository.deleteAll(distributions);
    }
}
