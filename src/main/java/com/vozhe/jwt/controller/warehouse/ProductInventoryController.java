package com.vozhe.jwt.controller.warehouse;

import com.vozhe.jwt.models.warehouse.ProductInventory;
import com.vozhe.jwt.service.ProductInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/whitelist/api/product-inventory")
public class ProductInventoryController {

    private final ProductInventoryService productInventoryService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductInventory>> getAllProductInventory() {
        List<ProductInventory> productInventories = productInventoryService.getAllProductInventory();
        return ResponseEntity.ok(productInventories);
    }
}
