package com.xyz.bankingapi.service.account;

import com.xyz.bankingapi.dto.AccountDetails;
import com.xyz.bankingapi.dto.TransferRequest;
import com.xyz.bankingapi.entity.Customer;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    AccountDetails getAccountDetails(String iban);

    boolean transfer(TransferRequest request);

    boolean createAccount(Customer customer);
}
