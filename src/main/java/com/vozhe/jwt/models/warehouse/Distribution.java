
package com.vozhe.jwt.models.warehouse;

import com.vozhe.jwt.enums.DistributionStatus;
import com.vozhe.jwt.models.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Distribution extends Base {

    private String requisitionNumber;

    private String outlet;

    @Enumerated(EnumType.STRING)
    private DistributionStatus status;

    /* ---- ACTORS ---- */
    private String requestedBy;   // SHOP
    private String approvedBy;    // ADMIN / STOCK_CONTROLLER
    private String issuedBy;      // STOCK_CONTROLLER
    private String driverName;    // DRIVER
    private String receivedBy;    // SHOP

    /* ---- TIMESTAMPS ---- */
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime issuedAt;
    private LocalDate issuedDate;
    private LocalDateTime deliveredAt;
    private LocalDateTime receivedAt;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "distribution_id")
    private List<DistributionItem> items;
    private Double totalWeight;
}
