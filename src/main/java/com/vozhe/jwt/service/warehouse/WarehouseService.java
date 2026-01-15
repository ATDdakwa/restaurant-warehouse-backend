
package com.vozhe.jwt.service.warehouse;

import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.exceptions.InvalidInputException;
import com.vozhe.jwt.models.settings.GlobalSettings;
import com.vozhe.jwt.models.warehouse.*;
import com.vozhe.jwt.repository.warehouse.*;
import com.vozhe.jwt.service.settings.GlobalSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final SupplierRepository supplierRepository;
    private final ReceivingRepository receivingRepository;
    private final InventoryRepository inventoryRepository;
    private final DistributionRepository distributionRepository;
    private final ProcessingRepository processingRepository;
    private final GlobalSettingsService globalSettingsService;


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
            Inventory inventory = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new InvalidInputException("Inventory item not found"));

            if (inventory.getPieces() == null || inventory.getPieces() < item.getRequestedPieces()) {
                throw new InvalidInputException("Not enough pieces for item " + inventory.getId());
            }

            inventory.setPieces(inventory.getPieces() - item.getRequestedPieces());
            inventoryRepository.save(inventory);
            
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

    public Distribution approveDistribution(Long id, List<DistributionItem> approvedItems) {
        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution record not found"));

        if (!"pending".equals(distribution.getStatus())) {
            throw new InvalidInputException("Only pending distributions can be approved");
        }

        GlobalSettings globalSettings = globalSettingsService.getGlobalSettings();
        if (globalSettings == null) {
            throw new InvalidInputException("Global settings for cost per kg not found. Please configure them.");
        }

        double totalApprovedWeight = 0;
        for (DistributionItem approvedItem : approvedItems) {
            DistributionItem existingItem = distribution.getItems().stream()
                    .filter(item -> item.getId().equals(approvedItem.getId()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidInputException("Distribution item not found: " + approvedItem.getId()));

            Inventory inventory = inventoryRepository.findById(existingItem.getInventoryId())
                    .orElseThrow(() -> new InvalidInputException("Inventory item not found: " + existingItem.getInventoryId()));

            if (inventory.getWeight() == null || inventory.getWeight() < approvedItem.getApprovedWeight()) {
                throw new InvalidInputException("Not enough stock (weight) for item " + inventory.getId());
            }

            inventory.setWeight(inventory.getWeight() - approvedItem.getApprovedWeight());
            inventoryRepository.save(inventory);

            // Calculate cost
            double costPerKg = 0.0;
            if (existingItem.getMeatType() == MeatType.CHICKEN) {
                if (globalSettings.getCostPerKgChicken() == null) {
                    throw new InvalidInputException("Cost per kg for Chicken not configured in global settings.");
                }
                costPerKg = globalSettings.getCostPerKgChicken();
            } else if (existingItem.getMeatType() == MeatType.BEEF) {
                if (globalSettings.getCostPerKgBeef() == null) {
                    throw new InvalidInputException("Cost per kg for Beef not configured in global settings.");
                }
                costPerKg = globalSettings.getCostPerKgBeef();
            }
            existingItem.setCost(approvedItem.getApprovedWeight() * costPerKg);

            existingItem.setApprovedWeight(approvedItem.getApprovedWeight());
            existingItem.setIssuedWeight(approvedItem.getApprovedWeight());
            totalApprovedWeight += approvedItem.getApprovedWeight();
        }

        distribution.setTotalWeight(totalApprovedWeight);
        distribution.setStatus("completed");
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
                .filter(i -> i.getMeatType() == MeatType.CHICKEN)
                .mapToDouble(Inventory::getWeight).sum();
        double totalStockBeef = availableInventory.stream()
                .filter(i -> i.getMeatType() == MeatType.BEEF)
                .mapToDouble(Inventory::getWeight).sum();

        double totalPiecesChicken = availableInventory.stream()
                .filter(i -> i.getMeatType() == MeatType.CHICKEN && i.getPieces() != null)
                .mapToInt(Inventory::getPieces).sum();
        double totalPiecesBeef = availableInventory.stream()
                .filter(i -> i.getMeatType() == MeatType.BEEF && i.getPieces() != null)
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
}
