package com.vozhe.jwt.repository.warehouse;

import com.vozhe.jwt.models.warehouse.Processing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingRepository extends JpaRepository<Processing, Long> {
    List<Processing> findByBatchNumber(String batchNumber);
}