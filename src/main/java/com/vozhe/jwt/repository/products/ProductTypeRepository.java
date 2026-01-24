package com.vozhe.jwt.repository.products;

import com.vozhe.jwt.models.products.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
}
