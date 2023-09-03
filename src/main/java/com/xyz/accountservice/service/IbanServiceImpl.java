package com.xyz.accountservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class IbanServiceImpl implements IbanService {
    public String generateIban(String countryCode, String accountNumber) {
        log.info("Before generating Iban number");
        //IBAN iban = IBAN.compose(countryCode, accountNumber);

        // Generate a random 2-digit check digit
        Random random = new Random();
        int checkDigit = random.nextInt(100);

        // Construct the IBAN
        String iban = countryCode + String.format("%02d", checkDigit) + accountNumber;

        log.info("after generating Iban number");
        return iban;
    }

    public String validateIban(String iban) {
        return "Valid Iban";
    }
}
