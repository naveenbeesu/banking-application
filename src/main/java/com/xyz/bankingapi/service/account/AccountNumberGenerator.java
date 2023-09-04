package com.xyz.bankingapi.service.account;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountNumberGenerator {

    public static String generateAccountNumber() {
        int length = 10;
        boolean useLetters = false;
        boolean useNumbers = true;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }
}
