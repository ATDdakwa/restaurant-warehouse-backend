
package com.vozhe.jwt.repository.warehouse;

import com.vozhe.jwt.models.warehouse.Distribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DistributionRepository extends JpaRepository<Distribution, Long> {
    List<Distribution> findByIssuedDateBetween(LocalDate startDate, LocalDate endDate);
}
