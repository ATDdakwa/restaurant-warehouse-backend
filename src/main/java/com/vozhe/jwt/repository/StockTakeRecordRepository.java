package com.vozhe.jwt.repository;

import com.vozhe.jwt.models.StockTakeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Import JpaSpecificationExecutor
import org.springframework.stereotype.Repository;

@Repository
public interface StockTakeRecordRepository extends JpaRepository<StockTakeRecord, Long>, JpaSpecificationExecutor<StockTakeRecord> {
}
