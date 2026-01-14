
package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.models.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProcessingOutput extends Base {
    private String cut;
    private Double weight;
    private Integer pieces;
    private Double yieldPercentage;
}
