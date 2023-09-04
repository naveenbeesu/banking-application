package com.xyz.bankingapi.service;

import com.xyz.bankingapi.dto.LogonRequest;
import com.xyz.bankingapi.dto.RegistrationRequest;
import com.xyz.bankingapi.dto.RegistrationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface CustomerService {
    RegistrationResponse register(RegistrationRequest request);

    String logon(LogonRequest request);

    String uploadImage(MultipartFile image, String username) throws IOException;

    byte[] downloadImage(String username);
}
