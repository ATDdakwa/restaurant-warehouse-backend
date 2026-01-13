
package com.vozhe.jwt.models.warehouse;

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
public class Distribution extends Base {
    private String requisitionNumber;
    private String outlet;
    private LocalDate issuedDate;
    private String issuedBy;
    private String approvedBy;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "distribution_id")
    private List<DistributionItem> items;
    private Double totalWeight;
    private String status;
}
