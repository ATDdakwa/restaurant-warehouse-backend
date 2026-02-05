package com.vozhe.jwt.service;

import com.vozhe.jwt.models.DryGoodsStockTakeRecord;
import com.vozhe.jwt.models.StockTakeRecord;
import com.vozhe.jwt.models.warehouse.ProductInventory;
import com.vozhe.jwt.payload.request.DryGoodsStockTakeRequest;
import com.vozhe.jwt.repository.warehouse.DryGoodsStockTakeRecordRepository;
import com.vozhe.jwt.repository.warehouse.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductInventoryService {

    private final ProductInventoryRepository productInventoryRepository;
    private final DryGoodsStockTakeRecordRepository dryGoodsStockTakeRecordRepository;

    @Transactional
    public void processDryGoodsStockTake(List<DryGoodsStockTakeRequest> requests) {
        for (DryGoodsStockTakeRequest request : requests) {
            Optional<ProductInventory> optionalProductInventory =
                    productInventoryRepository.findById(Long.valueOf(request.getItemId()));

            int systemQuantity;

            if (optionalProductInventory.isPresent()) {
                ProductInventory productInventory = optionalProductInventory.get();
                systemQuantity = productInventory.getQuantity(); // Capture BEFORE update
                productInventory.setQuantity(request.getActualQuantity());
                productInventoryRepository.save(productInventory);
            } else {
                systemQuantity = 0;
//                log.error("ProductInventory with ID {} not found for stock take", request.getItemId());
                // Consider: should you throw an exception here instead?
            }

            DryGoodsStockTakeRecord record = new DryGoodsStockTakeRecord();
            record.setItemId(request.getItemId());
            record.setProductName(request.getProductName());
            record.setActualQuantity(request.getActualQuantity());
            record.setSystemQuantity(systemQuantity);
            record.setQuantityVariance(systemQuantity - request.getActualQuantity());

            dryGoodsStockTakeRecordRepository.save(record);
        }
    }

    public List<ProductInventory> getAllProductInventory() {
        return productInventoryRepository.findAll();
    }

    public List<DryGoodsStockTakeRecord> getFilteredStockTakeRecords(String meatType, String itemType, LocalDateTime startDate, LocalDateTime endDate) {
        return dryGoodsStockTakeRecordRepository.findAll((Specification<DryGoodsStockTakeRecord>) (root, query, criteriaBuilder) -> {
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
