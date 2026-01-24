package com.vozhe.jwt.controller.products;


import com.vozhe.jwt.models.products.ProductType;
import com.vozhe.jwt.service.Impl.products.ProductTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/types")
@CrossOrigin
public class ProductTypeController {

    private final ProductTypeService service;

    public ProductTypeController(ProductTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductType> getAll() {
        return service.getAll();
    }

    @PostMapping
    public ProductType create(@RequestBody ProductType type) {
        return service.save(type);
    }

    @PutMapping("/{id}")
    public ProductType update(@PathVariable Long id, @RequestBody ProductType type) {
        return service.update(id, type);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

