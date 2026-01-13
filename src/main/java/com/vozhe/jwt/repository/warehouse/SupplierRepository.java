
package com.vozhe.jwt.repository.warehouse;

import com.vozhe.jwt.models.warehouse.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
