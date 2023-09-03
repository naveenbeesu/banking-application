package com.xyz.customer.service;

import com.xyz.customer.dto.LogonRequest;
import com.xyz.customer.dto.RegistrationRequest;
import com.xyz.customer.dto.RegistrationResponse;
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
