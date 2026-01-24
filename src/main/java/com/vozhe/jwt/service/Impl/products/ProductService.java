package com.vozhe.jwt.service.Impl.products;

import com.vozhe.jwt.models.products.Product;
import com.vozhe.jwt.models.products.ProductType;
import com.vozhe.jwt.repository.products.ProductRepository;
import com.vozhe.jwt.repository.products.ProductTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductTypeRepository typeRepository;

    public ProductService(ProductRepository productRepository, ProductTypeRepository typeRepository) {
        this.productRepository = productRepository;
        this.typeRepository = typeRepository;
    }

    public List<Product> getAllByType(Long typeId) {
        return productRepository.findByProductTypeId(typeId);
    }

    public Product save(Product product, Long typeId) {
        ProductType type = typeRepository.findById(typeId).orElseThrow(() -> new RuntimeException("Product type not found"));
        product.setProductType(type);
        return productRepository.save(product);
    }

    public Product update(Long id, Product product) {
        Product existing = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(product.getName());
        existing.setUnit(product.getUnit());
        existing.setPrice(product.getPrice());
        if (product.getProductType() != null) existing.setProductType(product.getProductType());
        return productRepository.save(existing);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}

