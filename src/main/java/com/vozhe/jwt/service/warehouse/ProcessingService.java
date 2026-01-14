
package com.vozhe.jwt.service.warehouse;

import com.vozhe.jwt.enums.ProcessingStatus;
import com.vozhe.jwt.exceptions.InvalidInputException;
import com.vozhe.jwt.models.warehouse.Inventory;
import com.vozhe.jwt.models.warehouse.Processing;
import com.vozhe.jwt.models.warehouse.ProcessingOutput;
import com.vozhe.jwt.models.warehouse.Receiving;
import com.vozhe.jwt.repository.warehouse.InventoryRepository;
import com.vozhe.jwt.repository.warehouse.ProcessingRepository;
import com.vozhe.jwt.repository.warehouse.ReceivingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final ProcessingRepository processingRepository;
    private final ReceivingRepository receivingRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public Processing saveProcessing(Processing processing) {
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
            inventoryRepository.findByBatchNumberAndCut(processing.getBatchNumber(), output.getCut())
                    .ifPresentOrElse(
                            inventory -> {
                                inventory.setWeight(inventory.getWeight() + output.getWeight());
                                if (output.getPieces() != null) {
                                    inventory.setPieces(inventory.getPieces() + output.getPieces());
                                }
                                inventoryRepository.save(inventory);
                            },
                            () -> {
                                Inventory newInventory = new Inventory();
                                newInventory.setBatchNumber(processing.getBatchNumber());
                                newInventory.setMeatType(processing.getMeatType());
                                newInventory.setCut(output.getCut());
                                newInventory.setWeight(output.getWeight());
                                newInventory.setPieces(output.getPieces());
                                newInventory.setStorageLocation(null); // Or determine based on cut
                                newInventory.setExpiryDate(LocalDate.now().plusDays(5)); // Example expiry
                                newInventory.setReceivedDate(null);
                                newInventory.setCostPerKg(0.0); // Example cost
                                newInventory.setStatus("available");
                                inventoryRepository.save(newInventory);
                            }
                    );
        }
    }
}
