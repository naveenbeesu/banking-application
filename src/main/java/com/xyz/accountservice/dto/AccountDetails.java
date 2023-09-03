package com.xyz.accountservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
@Setter
public class AccountDetails {
    private String accountNumber;
    private String iban;
    private BigDecimal balance;
    private String currency;
    private String accountType;
}
