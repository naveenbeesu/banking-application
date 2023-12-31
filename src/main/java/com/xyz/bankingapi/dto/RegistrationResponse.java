package com.xyz.bankingapi.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class RegistrationResponse {
    String status;
    String username;
    String password;
}
