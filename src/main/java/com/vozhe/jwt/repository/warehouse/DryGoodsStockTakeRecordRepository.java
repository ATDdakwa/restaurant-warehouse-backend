package com.vozhe.jwt.repository.warehouse;

import com.vozhe.jwt.models.DryGoodsStockTakeRecord;
import com.vozhe.jwt.models.StockTakeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DryGoodsStockTakeRecordRepository extends JpaRepository<DryGoodsStockTakeRecord, Long>, JpaSpecificationExecutor<DryGoodsStockTakeRecord> {
}
