
package com.vozhe.jwt.service.warehouse;

import com.vozhe.jwt.enums.DistributionStatus;
import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.exceptions.InvalidInputException;
import com.vozhe.jwt.models.settings.GlobalSettings;
import com.vozhe.jwt.models.settings.PaymentType;
import com.vozhe.jwt.models.warehouse.*;
import com.vozhe.jwt.payload.request.PaymentDto;
import com.vozhe.jwt.repository.warehouse.*;
import com.vozhe.jwt.service.settings.GlobalSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    public List<Receiving> getAllCreditReceiving() {
        return receivingRepository.findByPaymentType("Credit");
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

            inventory.setPieces(inventory.getPieces() - item.getRequestedPieces());
            inventoryRepository.save(inventory);

            item.setApprovedWeight(0.0); // Will be set during approval
            item.setIssuedWeight(0.0); // Will be set during approval
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
            DistributionItem existing = distribution.getItems().stream()
                    .filter(i -> i.getId().equals(approved.getId()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidInputException("Item not found"));

            existing.setApprovedWeight(approved.getApprovedWeight());
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

        GlobalSettings settings = globalSettingsService.getGlobalSettings();
        double totalIssuedWeight = 0;

        for (DistributionItem item : distribution.getItems()) {

            Inventory inventory = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new InvalidInputException("Inventory not found"));

            if (inventory.getWeight() < item.getApprovedWeight()) {
                throw new InvalidInputException("Insufficient stock for " + item.getCut());
            }

            inventory.setWeight(inventory.getWeight() - item.getApprovedWeight());
            inventoryRepository.save(inventory);

            double costPerKg =
                    item.getMeatType() == MeatType.CHICKEN
                            ? settings.getCostPerKgChicken()
                            : settings.getCostPerKgBeef();

            item.setIssuedWeight(item.getApprovedWeight());
            item.setCost(item.getIssuedWeight() * costPerKg);

            totalIssuedWeight += item.getIssuedWeight();
        }

        distribution.setTotalWeight(totalIssuedWeight);
        distribution.setIssuedAt(LocalDateTime.now());
        distribution.setStatus(DistributionStatus.ISSUED);

        return distributionRepository.save(distribution);
    }

    public Distribution confirmDelivery(Long id) {

        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.ISSUED) {
            throw new InvalidInputException("Only ISSUED distributions can be delivered");
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
}
