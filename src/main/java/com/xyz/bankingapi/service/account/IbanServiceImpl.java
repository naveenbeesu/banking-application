package com.xyz.bankingapi.service.account;

import com.xyz.bankingapi.exceptions.InvalidIbanException;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.*;
import org.springframework.stereotype.Service;

import static com.xyz.bankingapi.utils.Constants.INVALID_IBAN;

@Service
@Slf4j
public class IbanServiceImpl implements IbanService {

    public String generateIban(String countryCode, String accountNumber, String bankCode) {
        log.info("Before generating Iban number");
        Iban iban = new Iban.Builder()
                .countryCode(CountryCode.valueOf(countryCode))
                .bankCode(bankCode)
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
            log.error("Invalid iban" + e.getMessage());
            throw new InvalidIbanException("iban", INVALID_IBAN);
        }
    }

    public boolean areSameBank(String senderIban, String receiverIban, String bankCode) {
        try {
            return IbanUtil.getBankCode(senderIban).equals(bankCode) || IbanUtil.getBankCode(receiverIban).equals(bankCode);
        } catch (IbanFormatException |
                 InvalidCheckDigitException |
                 UnsupportedCountryException e) {
            // invalid
            log.error("Invalid iban" + e.getMessage());
            throw new InvalidIbanException("iban", "Invalid Iban");
        }
    }
}
