package com.vozhe.jwt.service.Impl.products;


import com.vozhe.jwt.models.products.ProductType;
import com.vozhe.jwt.repository.products.ProductTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductTypeService {

    private final ProductTypeRepository repository;

    public ProductTypeService(ProductTypeRepository repository) {
        this.repository = repository;
    }

    public List<ProductType> getAll() {
        return repository.findAll();
    }

    public ProductType getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Product type not found"));
    }

    public ProductType save(ProductType type) {
        return repository.save(type);
    }

    public ProductType update(Long id, ProductType type) {
        ProductType existing = getById(id);
        existing.setName(type.getName());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

