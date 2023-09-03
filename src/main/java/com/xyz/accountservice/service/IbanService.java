package com.xyz.accountservice.service;

import org.springframework.stereotype.Service;

@Service
public interface IbanService {
    String generateIban(String countryCode, String accountNumber);

    String validateIban(String iban);
}
