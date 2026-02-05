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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"requisitionNumber"})})
public class DryGoodsDistribution extends Base {
    private String requisitionNumber;
    private String outlet;
    @Enumerated(EnumType.STRING)
    private DistributionStatus status;

    /* ---- ACTORS ---- */
    private String requestedBy;
    private String approvedBy;
    private String issuedBy;
    private String driverName;
    private String receivedBy;

    /* ---- TIMESTAMPS ---- */
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime issuedAt;
    private LocalDate issuedDate;
    private LocalDateTime deliveredAt;
    private LocalDateTime receivedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "dry_goods_distribution_id")
    private List<DryGoodsDistributionItem> productItems;
}
