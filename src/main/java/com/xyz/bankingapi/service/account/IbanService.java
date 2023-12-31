package com.xyz.bankingapi.service.account;

import org.springframework.stereotype.Service;

@Service
public interface IbanService {
    String generateIban(String countryCode, String accountNumber, String bankCode);

    void validateIban(String iban);

    boolean areSameBank(String senderIban, String receiverIban, String bankCode);
}
