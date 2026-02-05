package com.vozhe.jwt.service;

import com.vozhe.jwt.models.StockTakeRecord;
import com.vozhe.jwt.models.warehouse.Inventory;
import com.vozhe.jwt.repository.StockTakeRecordRepository;
import com.vozhe.jwt.repository.warehouse.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockTakeRecordService {

    private final StockTakeRecordRepository stockTakeRecordRepository;
    private final InventoryRepository inventoryRepository;

    public List<StockTakeRecord> saveStockTakeRecords(List<StockTakeRecord> records) {
        for (StockTakeRecord request : records) {
            Optional<Inventory> optionalInventory = inventoryRepository.findById(Long.valueOf(request.getItemId()));
            if (optionalInventory.isPresent()) {
                Inventory inventory = optionalInventory.get();
                inventory.setPieces(request.getActualPieces());
                inventory.setWeight(request.getActualWeight());
                inventoryRepository.save(inventory);
            } else {
                // TODO: Handle case where MeatsInventory item is not found (e.g., log, throw exception)
                System.err.println("Inventory with ID " + request.getItemId() + " not found for stock take.");
            }
        }
        return stockTakeRecordRepository.saveAll(records);
    }

    public List<StockTakeRecord> getFilteredStockTakeRecords(String meatType, String itemType, LocalDateTime startDate, LocalDateTime endDate) {
        return stockTakeRecordRepository.findAll((Specification<StockTakeRecord>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>(); // Explicitly use jakarta.persistence.criteria.Predicate

            if (meatType != null && !meatType.isEmpty()) {
                // As noted before, meatType is not directly in StockTakeRecord.
                // To implement this, StockTakeRecord entity would need a meatType field,
                // or a join would be required. For now, this filter remains unimplemented.
            }

            if (itemType != null && !itemType.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("itemType"), itemType));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
