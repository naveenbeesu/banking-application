package com.xyz.accountservice.service;

import com.xyz.accountservice.dto.AccountDetails;
import com.xyz.accountservice.dto.TransferRequest;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    AccountDetails getDetails(Long accountId);

    boolean transfer(TransferRequest request);

    String createAccount(long customerId);
}
