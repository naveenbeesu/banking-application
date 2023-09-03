package com.xyz.customer.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String username;
    private LocalDate dateOfBirth;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String mobileNumber;
    private String nationalId;
    private String password;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoginTime;
}
