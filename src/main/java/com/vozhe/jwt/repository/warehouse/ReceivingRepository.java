
package com.vozhe.jwt.repository.warehouse;

import com.vozhe.jwt.models.warehouse.Receiving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivingRepository extends JpaRepository<Receiving, Long> {
    List<Receiving> findByPaymentType(String paymentType);
    @Query("SELECT DISTINCT r FROM Receiving r LEFT JOIN FETCH r.meatType")
    List<Receiving> findAllWithMeatType();
}
