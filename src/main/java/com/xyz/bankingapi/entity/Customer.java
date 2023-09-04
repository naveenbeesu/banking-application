package com.xyz.bankingapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    String firstName;
    String lastName;
    String username;
    LocalDate dateOfBirth;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    Address address;
    String mobileNumber;
    String nationalId;
    String password;
    LocalDateTime registrationDate;
    LocalDateTime lastLoginTime;
    @Lob
    @Column(name = "id_document",length = 1000)
    private byte[] idDocument;
}
