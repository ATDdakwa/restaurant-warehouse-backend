package com.vozhe.jwt.repository.products;

import com.vozhe.jwt.models.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductTypeId(Long productType_id);
}
