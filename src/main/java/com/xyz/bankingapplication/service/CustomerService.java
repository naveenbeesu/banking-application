package com.xyz.bankingapplication.service;

import com.xyz.bankingapplication.dto.LogonRequest;
import com.xyz.bankingapplication.dto.RegistrationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface CustomerService {
    String register(RegistrationRequest request);

    String login(LogonRequest request);

    String uploadImage(MultipartFile image, String username) throws IOException;

    byte[] downloadImage(String username);
}