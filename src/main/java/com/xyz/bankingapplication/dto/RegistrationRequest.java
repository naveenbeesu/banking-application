package com.xyz.bankingapplication.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RegistrationRequest {
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
