
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
public class Supplier extends Base {
    private String name;
    private String contact;
    private String email;
    private String address;
}
