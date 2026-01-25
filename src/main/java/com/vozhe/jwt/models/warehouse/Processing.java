
package com.vozhe.jwt.models.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.vozhe.jwt.enums.MeatType;
import com.vozhe.jwt.models.Base;
import com.vozhe.jwt.models.Meat;
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

    @ManyToOne
    @JoinColumn(name = "meat_type_id")
    private Meat meatType;

    // Transient field to accept string input from frontend
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String meatTypeName;

    private Integer quantity; // birds/carcasses processed in this step
    private Double weight; // weight processed in this step
    private LocalDate processedDate;
    private String processedBy;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "processing_id")
    private List<ProcessingOutput> outputs;

    private Double totalOutputWeight;
    private Double wastageWeight;
    private Double yieldPercentage;
    private Boolean isCompleted;

    // Handle string input for meatType
    @JsonSetter("meatType")
    public void setMeatTypeAsString(String meatName) {
        this.meatTypeName = meatName;
    }
}
