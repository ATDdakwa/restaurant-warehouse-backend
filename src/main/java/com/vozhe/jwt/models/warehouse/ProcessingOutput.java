
package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.BeefCut;
import com.vozhe.jwt.enums.ChickenCut;
import com.vozhe.jwt.models.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProcessingOutput extends Base {
    @Enumerated(EnumType.STRING)
    private ChickenCut chickenCut;
    @Enumerated(EnumType.STRING)
    private BeefCut beefCut;
    private Double weight;
    private Double yieldPercentage;
}
