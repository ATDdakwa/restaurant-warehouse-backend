package com.vozhe.jwt.service.warehouse;

import com.vozhe.jwt.enums.DistributionStatus;
import com.vozhe.jwt.exceptions.InvalidInputException;
import com.vozhe.jwt.models.warehouse.Distribution;
import com.vozhe.jwt.models.warehouse.DryGoodsDistribution;
import com.vozhe.jwt.models.warehouse.DryGoodsDistributionItem;
import com.vozhe.jwt.models.warehouse.ProductInventory;
import com.vozhe.jwt.repository.warehouse.DryGoodsDistributionRepository;
import com.vozhe.jwt.repository.warehouse.ProductInventoryRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DryGoodsDistributionService {

    private final DryGoodsDistributionRepository dryGoodsDistributionRepository;
    private final ProductInventoryRepository productInventoryRepository;

    @Transactional
    public List<DryGoodsDistribution> getAllDistributions() {
        List<DryGoodsDistribution> distributions = dryGoodsDistributionRepository.findAll();
        distributions.forEach(distribution -> distribution.getProductItems().size()); // Initialize items
        return distributions;
    }

    @Transactional
    public DryGoodsDistribution requestDistribution(DryGoodsDistribution distribution) {
        if (distribution.getProductItems() == null || distribution.getProductItems().isEmpty()) {
            throw new InvalidInputException("Distribution items are required");
        }

        for (DryGoodsDistributionItem item : distribution.getProductItems()) {
            ProductInventory product = productInventoryRepository.findById(item.getProductInventoryId())
                    .orElseThrow(() -> new InvalidInputException("Product not found"));

            if (product.getQuantity() < item.getRequestedQuantity()) {
                throw new InvalidInputException("Not enough quantity for " + item.getProductName());
            }

            item.setApprovedQuantity(0);
            item.setIssuedQuantity(0);
        }

        distribution.setStatus(DistributionStatus.REQUESTED);
        distribution.setRequestedAt(LocalDateTime.now());

        return dryGoodsDistributionRepository.save(distribution);
    }

    @Transactional
    public DryGoodsDistribution approveDistribution(Long id, List<DryGoodsDistributionItem> approvedItems) {
        DryGoodsDistribution distribution = dryGoodsDistributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.REQUESTED &&
                distribution.getStatus() != DistributionStatus.APPROVED) {
            throw new InvalidInputException("Only REQUESTED or APPROVED distributions can be (re-)approved");
        }

        for (DryGoodsDistributionItem approved : approvedItems) {
            DryGoodsDistributionItem existing = distribution.getProductItems().stream()
                    .filter(i -> i.getId().equals(approved.getId()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidInputException("Item not found"));

            existing.setApprovedQuantity(approved.getApprovedQuantity());
        }

        distribution.setStatus(DistributionStatus.APPROVED);
        distribution.setApprovedAt(LocalDateTime.now());

        return dryGoodsDistributionRepository.save(distribution);
    }

    @Transactional
    public DryGoodsDistribution issueDistribution(Long id) {
        DryGoodsDistribution distribution = dryGoodsDistributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.APPROVED) {
            throw new InvalidInputException("Only APPROVED distributions can be issued");
        }

        for (DryGoodsDistributionItem item : distribution.getProductItems()) {
            ProductInventory product = productInventoryRepository.findById(item.getProductInventoryId())
                    .orElseThrow(() -> new InvalidInputException("Product not found"));

            if (product.getQuantity() < item.getApprovedQuantity()) {
                throw new InvalidInputException("Insufficient stock for " + item.getProductName());
            }

            // Deduct from inventory
            product.setQuantity(product.getQuantity() - item.getApprovedQuantity());
            productInventoryRepository.save(product);

            // Set issued quantity
            item.setIssuedQuantity(item.getApprovedQuantity());

            // Calculate cost (if you have pricing)
            item.setCost(item.getApprovedQuantity() * product.getPrice());
        }

        distribution.setIssuedAt(LocalDateTime.now());
        distribution.setStatus(DistributionStatus.ISSUED);

        return dryGoodsDistributionRepository.save(distribution);
    }

    // Similar methods for acknowledge, deliver, receive...
    public DryGoodsDistribution acknowledgeDelivery(Long id) {
        DryGoodsDistribution distribution = dryGoodsDistributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.ISSUED) {
            throw new InvalidInputException("Only ISSUED distributions can be acknowledged");
        }

        distribution.setStatus(DistributionStatus.ACKNOWLEDGED);
        distribution.setDeliveredAt(LocalDateTime.now());

        return dryGoodsDistributionRepository.save(distribution);
    }

    public DryGoodsDistribution confirmDelivery(Long id) {
        DryGoodsDistribution distribution = dryGoodsDistributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.ACKNOWLEDGED) {
            throw new InvalidInputException("Only ACKNOWLEDGED distributions can be delivered");
        }

        distribution.setStatus(DistributionStatus.DELIVERED);
        distribution.setDeliveredAt(LocalDateTime.now());

        return dryGoodsDistributionRepository.save(distribution);
    }

    public DryGoodsDistribution confirmReceipt(Long id) {
        DryGoodsDistribution distribution = dryGoodsDistributionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Distribution not found"));

        if (distribution.getStatus() != DistributionStatus.DELIVERED) {
            throw new InvalidInputException("Only DELIVERED distributions can be received");
        }

        distribution.setStatus(DistributionStatus.RECEIVED);
        distribution.setReceivedAt(LocalDateTime.now());

        return dryGoodsDistributionRepository.save(distribution);
    }
}