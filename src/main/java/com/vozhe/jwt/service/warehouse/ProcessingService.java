
package com.vozhe.jwt.service.warehouse;

import com.vozhe.jwt.enums.ProcessingStatus;
import com.vozhe.jwt.exceptions.InvalidInputException;
import com.vozhe.jwt.models.Meat;
import com.vozhe.jwt.models.warehouse.Inventory;
import com.vozhe.jwt.models.warehouse.Processing;
import com.vozhe.jwt.models.warehouse.ProcessingOutput;
import com.vozhe.jwt.models.warehouse.Receiving;
import com.vozhe.jwt.repository.MeatRepository;
import com.vozhe.jwt.repository.warehouse.InventoryRepository;
import com.vozhe.jwt.repository.warehouse.ProcessingRepository;
import com.vozhe.jwt.repository.warehouse.ReceivingRepository;
import com.vozhe.jwt.service.settings.GlobalSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final ProcessingRepository processingRepository;
    private final ReceivingRepository receivingRepository;
    private final InventoryRepository inventoryRepository;
    private final MeatRepository meatRepository;
    private final GlobalSettingsService globalSettingsService; // Add this



    @Transactional
    public Processing saveProcessing(Processing processing) {
        // Convert string to Meat entity
        if (processing.getMeatTypeName() != null && !processing.getMeatTypeName().isEmpty()) {
            Meat meat = meatRepository.findByName(processing.getMeatTypeName())
                    .orElseThrow(() -> new RuntimeException("Meat type not found: " + processing.getMeatTypeName()));
            processing.setMeatType(meat);
        }

        // 1. Find the receiving record
        Receiving receiving = receivingRepository.findById(processing.getReceivingId())
                .orElseThrow(() -> new InvalidInputException("Receiving record not found with id: " + processing.getReceivingId()));

        // 2. Validate the input
        validateProcessingInput(processing, receiving);

        // 3. Calculate yield for the current step
        calculateYield(processing);

        // 4. Save the processing record
        Processing savedProcessing = processingRepository.save(processing);

        // 5. Update the receiving record
        updateReceivingRecord(processing, receiving);

        // 6. Create or update inventory
        updateInventory(savedProcessing);

        return savedProcessing;
    }

    public List<Processing> getProcessingHistory(String batchNumber) {
        return processingRepository.findByBatchNumber(batchNumber);
    }

    public List<Processing> getAllProcessings() {
        return processingRepository.findAll();
    }

    private void validateProcessingInput(Processing processing, Receiving receiving) {
        double remainingWeight = receiving.getTotalWeight() - receiving.getProcessedWeight();
        int remainingQuantity = receiving.getQuantity() - receiving.getProcessedQuantity();

        if (processing.getWeight() > remainingWeight) {
            throw new InvalidInputException("Processing weight cannot be greater than the remaining weight in the batch.");
        }
        if (processing.getQuantity() > remainingQuantity) {
            throw new InvalidInputException("Processing quantity cannot be greater than the remaining quantity in the batch.");
        }
    }

    private void calculateYield(Processing processing) {
        double totalOutputWeight = processing.getOutputs().stream()
                .mapToDouble(ProcessingOutput::getWeight)
                .sum();
        processing.setTotalOutputWeight(totalOutputWeight);

        double wastageWeight = processing.getWeight() - totalOutputWeight;
        processing.setWastageWeight(wastageWeight);

        if (processing.getWeight() > 0) {
            double yieldPercentage = (totalOutputWeight / processing.getWeight()) * 100;
            processing.setYieldPercentage(yieldPercentage);
        } else {
            processing.setYieldPercentage(0.0);
        }
    }

    private void updateReceivingRecord(Processing processing, Receiving receiving) {
        receiving.setProcessedWeight(receiving.getProcessedWeight() + processing.getWeight());
        receiving.setProcessedQuantity(receiving.getProcessedQuantity() + processing.getQuantity());

        if (processing.getIsCompleted() ||
                (receiving.getProcessedWeight() >= receiving.getTotalWeight() &&
                        receiving.getProcessedQuantity() >= receiving.getQuantity())) {
            receiving.setStatus(ProcessingStatus.COMPLETED);
        } else {
            receiving.setStatus(ProcessingStatus.PARTIALLY_PROCESSED);
        }
        receivingRepository.save(receiving);
    }

    private void updateInventory(Processing processing) {
        for (ProcessingOutput output : processing.getOutputs()) {
            // Find by meatType and cut ONLY (no batch number)
            Inventory inventory = inventoryRepository
                    .findByMeatTypeAndCut(processing.getMeatType(), output.getCut())
                    .orElseGet(() -> {
                        Double costPerKg = globalSettingsService.getCostPerKg(
                                processing.getMeatType().getId()
                        );

                        Inventory newInventory = new Inventory();
                        newInventory.setBatchNumber(null); // Don't track batch in inventory
                        newInventory.setMeatType(processing.getMeatType());
                        newInventory.setCut(output.getCut());
                        newInventory.setWeight(0.0);
                        newInventory.setPieces(0);
                        newInventory.setStorageLocation(null);
                        newInventory.setExpiryDate(LocalDate.now().plusDays(5));
                        newInventory.setCostPerKg(costPerKg);
                        newInventory.setStatus("available");
                        newInventory.setSourceBatches(processing.getBatchNumber()); // Initialize with first batch
                        newInventory.setLastUpdated(LocalDateTime.now());
                        return newInventory;
                    });

            // Track which batches contributed (for recall/traceability purposes)
            if (inventory.getSourceBatches() == null || inventory.getSourceBatches().isEmpty()) {
                inventory.setSourceBatches(processing.getBatchNumber());
            } else if (!inventory.getSourceBatches().contains(processing.getBatchNumber())) {
                // Only add if this batch hasn't contributed before
                inventory.setSourceBatches(inventory.getSourceBatches() + "," + processing.getBatchNumber()
                );
            }

            // Add to existing inventory
            inventory.setWeight(inventory.getWeight() + output.getWeight());
            inventory.setPieces((inventory.getPieces() != null ? inventory.getPieces() : 0) + (output.getPieces() != null ? output.getPieces() : 0)
            );

            // Update the timestamp
            inventory.setLastUpdated(LocalDateTime.now());

            inventoryRepository.save(inventory);
        }
    }
}