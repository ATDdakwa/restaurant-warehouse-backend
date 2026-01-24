package com.vozhe.jwt.models.products;

import com.vozhe.jwt.models.Base;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product extends Base {
    private String name;
    private String unit;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "product_type_id", nullable = false)
    private ProductType productType;
}
