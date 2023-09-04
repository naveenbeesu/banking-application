package com.xyz.bankingapi.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomerDto {
    String firstName;
    String lastName;
    String username;
    LocalDate dateOfBirth;
    String address;
    String mobileNumber;
    String nationalId;
    String password;
    LocalDateTime registrationDate;
    LocalDateTime lastLoginDate;
}
