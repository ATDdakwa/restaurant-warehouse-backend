
package com.vozhe.jwt.repository.warehouse;

import com.vozhe.jwt.models.warehouse.Receiving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceivingRepository extends JpaRepository<Receiving, Long> {
}
