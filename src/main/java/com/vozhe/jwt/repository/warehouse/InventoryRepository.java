package com.vozhe.jwt.repository.warehouse;

import com.vozhe.jwt.models.warehouse.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByBatchNumberAndCut(String batchNumber, String cut);
}