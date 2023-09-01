package com.xyz.bankingapplication.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    String address;
    String mobileNumber;
    String nationalId;
    String password;
    LocalDateTime registrationDate;
    LocalDateTime lastLoginTime;
}
