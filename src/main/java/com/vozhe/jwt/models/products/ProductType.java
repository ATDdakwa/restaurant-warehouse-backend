package com.vozhe.jwt.models.products;

import com.vozhe.jwt.models.Base;
import lombok.*;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductType extends Base {
    private String name;
}

