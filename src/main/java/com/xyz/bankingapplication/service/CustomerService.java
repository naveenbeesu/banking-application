package com.xyz.bankingapplication.service;

import com.xyz.bankingapplication.dto.LogonRequest;
import com.xyz.bankingapplication.dto.RegistrationRequest;
import org.springframework.stereotype.Service;

@Service
public interface CustomerService {
    String register(RegistrationRequest request);

    String login(LogonRequest request);
}
