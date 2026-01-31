
package com.vozhe.jwt.repository.warehouse;

import com.vozhe.jwt.enums.DistributionStatus;
import com.vozhe.jwt.models.warehouse.Distribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DistributionRepository extends JpaRepository<Distribution, Long> {
    List<Distribution> findByIssuedDateBetween(LocalDate startDate, LocalDate endDate);
    List<Distribution> findByStatus(DistributionStatus status);
    List<Distribution> findByStatusIn(List<DistributionStatus> statuses);
    @Modifying
    @Transactional
    @Query("DELETE FROM DistributionItem di WHERE di.id IN " +
            "(SELECT d.id FROM Distribution d WHERE d.status = :status)")
    void deleteItemsByDistributionStatus(@Param("status") DistributionStatus status);

    @Modifying
    @Transactional
    @Query("DELETE FROM Distribution d WHERE d.status = :status")
    void deleteByStatus(@Param("status") DistributionStatus status);

}
