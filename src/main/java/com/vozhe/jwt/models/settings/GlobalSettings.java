package com.vozhe.jwt.models.settings;

import com.vozhe.jwt.models.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GlobalSettings extends Base {
    private Double costPerKgChicken;
    private Double costPerKgBeef;
}
