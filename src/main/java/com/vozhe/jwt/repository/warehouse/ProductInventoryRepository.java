
package com.vozhe.jwt.repository.warehouse;

import com.vozhe.jwt.models.warehouse.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    Optional<ProductInventory> findByProductIdAndProductName(String productId, String productName);

}
