package com.vozhe.jwt.controller.products;


import com.vozhe.jwt.models.products.Product;
import com.vozhe.jwt.service.Impl.products.ProductService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> getByType(@RequestParam Long typeId) {
        return service.getAllByType(typeId);
    }

    @PostMapping
    public Product create(@RequestBody ProductRequest request) {
        return service.save(request.toProduct(), request.getProductTypeId());
    }

    @Data
    public static class ProductRequest {
        private String name;
        private String unit;
        private Double price;
        private Long productTypeId; // <-- change here

        public Product toProduct() {
            Product p = new Product();
            p.setName(name);
            p.setUnit(unit);
            p.setPrice(price);
            return p;
        }
    }


    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return service.update(id, product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
