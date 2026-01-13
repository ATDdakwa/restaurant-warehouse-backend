
package com.vozhe.jwt.service.warehouse;

import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.enums.StorageLocation;
import com.vozhe.jwt.exceptions.InvalidInputException;
import com.vozhe.jwt.models.warehouse.*;
import com.vozhe.jwt.repository.warehouse.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final SupplierRepository supplierRepository;
    private final ReceivingRepository receivingRepository;
    private final ProcessingRepository processingRepository;
    private final InventoryRepository inventoryRepository;
    private final DistributionRepository distributionRepository;

    // Supplier actions
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    // Receiving actions
    public Receiving saveReceiving(Receiving receiving) {
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

    public List<Receiving> getAllReceivings() {
        return receivingRepository.findAll();
    }

    // Processing actions
    public Processing saveProcessing(Processing processing) {
        // Business logic for processing
        if (processing.getReceivingId() == null) {
            throw new InvalidInputException("Receiving ID is required");
        }
        Receiving receiving = receivingRepository.findById(processing.getReceivingId())
                .orElseThrow(() -> new InvalidInputException("Receiving record not found"));

        if (processing.getInputWeight() > receiving.getTotalWeight()) {
            throw new InvalidInputException("Input weight cannot be greater than the received weight");
        }

        double totalOutputWeight = processing.getOutputs().stream()
                .mapToDouble(ProcessingOutput::getWeight)
                .sum();
        processing.setTotalOutputWeight(totalOutputWeight);

        double wastageWeight = processing.getInputWeight() - totalOutputWeight;
        processing.setWastageWeight(wastageWeight);

        double yieldPercentage = (totalOutputWeight / processing.getInputWeight()) * 100;
        processing.setYieldPercentage(yieldPercentage);

        Processing savedProcessing = processingRepository.save(processing);

        // Create inventory items from outputs
        for (ProcessingOutput output : savedProcessing.getOutputs()) {
            Inventory inventory = new Inventory();
            inventory.setBatchNumber(savedProcessing.getBatchNumber());
            inventory.setMeatType(savedProcessing.getMeatType());
            if (savedProcessing.getMeatType() == MeatType.CHICKEN) {
                inventory.setChickenCut(output.getChickenCut());
            } else {
                inventory.setBeefCut(output.getBeefCut());
            }
            inventory.setWeight(output.getWeight());
            inventory.setStorageLocation(StorageLocation.CHILLER); // Or determine based on cut
            inventory.setExpiryDate(LocalDate.now().plusDays(5)); // Example expiry
            inventory.setReceivedDate(receiving.getDeliveryDate());
            inventory.setCostPerKg(0.0); // Example cost
            inventory.setStatus("available");
            inventoryRepository.save(inventory);
        }

        return savedProcessing;
    }

    public List<Processing> getAllProcessings() {
        return processingRepository.findAll();
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

        double totalWeight = 0;
        for (DistributionItem item : distribution.getItems()) {
            Inventory inventory = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new InvalidInputException("Inventory item not found"));

            if (inventory.getWeight() < item.getRequestedWeight()) {
                throw new InvalidInputException("Not enough stock for item " + inventory.getId());
            }

            inventory.setWeight(inventory.getWeight() - item.getRequestedWeight());
            item.setIssuedWeight(item.getRequestedWeight());
            inventoryRepository.save(inventory);
            totalWeight += item.getIssuedWeight();
        }

        distribution.setTotalWeight(totalWeight);
        return distributionRepository.save(distribution);
    }

    public List<Distribution> getAllDistributions() {
        return distributionRepository.findAll();
    }

    public Distribution approveDistribution(Long id) {
        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution record not found"));

        if (!"pending".equals(distribution.getStatus())) {
            throw new InvalidInputException("Only pending distributions can be approved");
        }

        for (DistributionItem item : distribution.getItems()) {
            Inventory inventory = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new InvalidInputException("Inventory item not found"));

            if (inventory.getWeight() < item.getRequestedWeight()) {
                throw new InvalidInputException("Not enough stock for item " + inventory.getId());
            }

            inventory.setWeight(inventory.getWeight() - item.getRequestedWeight());
            inventoryRepository.save(inventory);
        }

        distribution.setStatus("completed");
        return distributionRepository.save(distribution);
    }

    public com.vozhe.jwt.payload.response.DashboardMetrics getDashboardMetrics() {
        double totalReceived = receivingRepository.findAll().stream().mapToDouble(Receiving::getTotalWeight).sum();
        double totalProcessed = processingRepository.findAll().stream().mapToDouble(Processing::getTotalOutputWeight).sum();
        double totalIssued = distributionRepository.findAll().stream()
                .filter(d -> "completed".equals(d.getStatus()))
                .mapToDouble(Distribution::getTotalWeight).sum();
        double currentStock = inventoryRepository.findAll().stream()
                .filter(i -> "available".equals(i.getStatus()))
                .mapToDouble(Inventory::getWeight).sum();
        double averageYield = processingRepository.findAll().stream().mapToDouble(Processing::getYieldPercentage).average().orElse(0);
        double totalWastage = processingRepository.findAll().stream().mapToDouble(Processing::getWastageWeight).sum();
        double wastagePercentage = totalReceived > 0 ? (totalWastage / totalReceived) * 100 : 0;
        long pendingRequisitions = distributionRepository.findAll().stream().filter(d -> "pending".equals(d.getStatus())).count();

        return new com.vozhe.jwt.payload.response.DashboardMetrics(
                totalReceived,
                totalProcessed,
                totalIssued,
                currentStock,
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
}
