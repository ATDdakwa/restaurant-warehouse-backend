package com.vozhe.jwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity<String> {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String idNumber;
    private String address;
    private Date DOB;
    private String password;
    @Column(unique=true)
    private String username;
    private String roles;
    private String shopName;

}
