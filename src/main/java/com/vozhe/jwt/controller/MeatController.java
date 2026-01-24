package com.vozhe.jwt.controller;

import com.vozhe.jwt.models.Meat;
import com.vozhe.jwt.models.OutputCut;
import com.vozhe.jwt.service.MeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RestController
@RequestMapping("/api/meats")
@RequiredArgsConstructor
@CrossOrigin
public class MeatController {

    private final MeatService meatService;

    @GetMapping
    public List<Meat> getAllMeats() {
        return meatService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meat> getMeatById(@PathVariable Long id) {
        Meat meat = meatService.findById(id);
        if (meat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(meat);
    }

    @PostMapping
    public Meat createMeat(@RequestBody Meat meat) {
        // Set parent reference for all output cuts
        if (meat.getOutputCuts() != null) {
            for (OutputCut cut : meat.getOutputCuts()) {
                cut.setMeat(meat);
            }
        }
        return meatService.save(meat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Meat> updateMeat(@PathVariable Long id, @RequestBody Meat meatDetails) {
        Meat meat = meatService.findById(id);
        if (meat == null) {
            return ResponseEntity.notFound().build();
        }

        meat.setName(meatDetails.getName());

        // Clear and repopulate the collection
        meat.getOutputCuts().clear();

        if (meatDetails.getOutputCuts() != null) {
            for (OutputCut cut : meatDetails.getOutputCuts()) {
                cut.setMeat(meat);  // Set the parent reference
                meat.getOutputCuts().add(cut);
            }
        }

        Meat updatedMeat = meatService.save(meat);
        return ResponseEntity.ok(updatedMeat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeat(@PathVariable Long id) {
        Meat meat = meatService.findById(id);
        if (meat == null) {
            return ResponseEntity.notFound().build();
        }
        meatService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}