
package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.models.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Processing extends Base {
    private Long receivingId;
    private String batchNumber;
    @Enumerated(EnumType.STRING)
    private MeatType meatType;
    private Double inputWeight;
    private LocalDate processedDate;
    private String processedBy;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "processing_id")
    private List<ProcessingOutput> outputs;
    private Double totalOutputWeight;
    private Double wastageWeight;
    private Double yieldPercentage;
}
