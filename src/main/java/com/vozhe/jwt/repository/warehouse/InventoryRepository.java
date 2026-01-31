package com.vozhe.jwt.repository.warehouse;

import com.vozhe.jwt.models.Meat;
import com.vozhe.jwt.models.warehouse.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByBatchNumberAndCut(String batchNumber, String cut);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventory> findByMeatTypeAndCut(Meat meatType, String cut);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :id")
    Optional<Inventory> findByIdForUpdate(Long id);
}