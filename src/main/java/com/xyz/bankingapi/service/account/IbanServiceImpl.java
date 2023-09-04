package com.xyz.bankingapi.service.account;

import com.xyz.bankingapi.exceptions.InvalidIbanException;
import com.xyz.bankingapi.utils.AccountProperties;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IbanServiceImpl implements IbanService {

    @Qualifier("accountProperties")
    @Autowired
    AccountProperties accountProperties;


    public String generateIban(String countryCode, String accountNumber) {
        log.info("Before generating Iban number");
        Iban iban = new Iban.Builder()
                            .countryCode(CountryCode.valueOf(countryCode))
                            .bankCode(accountProperties.getBankCode())
                            .accountNumber(accountNumber)
                            .build();
        log.info("after generating Iban number");
        return iban.toString();
    }

    public void validateIban(String iban) {
        try {
            IbanUtil.validate(iban);
            log.info("After iban validation");

            // valid
        } catch (IbanFormatException |
                 InvalidCheckDigitException |
                 UnsupportedCountryException e) {
            // invalid
            log.error("Invalid iban"+ e.getMessage());
            throw new InvalidIbanException("iban", "Invalid Iban");
        }
    }
    public boolean areSameBank(String senderIban, String receiverIban) {
        try {
            return IbanUtil.getBankCode(senderIban).equals(accountProperties.getBankCode()) || IbanUtil.getBankCode(receiverIban).equals(accountProperties.getBankCode());
        } catch (IbanFormatException |
                 InvalidCheckDigitException |
                 UnsupportedCountryException e) {
            // invalid
            log.error("Invalid iban"+ e.getMessage());
            throw new InvalidIbanException("iban", "Invalid Iban");
        }
    }
}
