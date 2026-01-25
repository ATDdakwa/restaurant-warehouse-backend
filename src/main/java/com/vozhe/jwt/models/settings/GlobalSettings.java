package com.vozhe.jwt.models.settings;

import com.vozhe.jwt.models.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GlobalSettings extends Base {

    // Store meat costs as a map: meat_type_id -> cost_per_kg
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "meat_costs", joinColumns = @JoinColumn(name = "settings_id"))
    @MapKeyColumn(name = "meat_type_id")
    @Column(name = "cost_per_kg")
    private Map<Long, Double> meatCosts = new HashMap<>();
}
