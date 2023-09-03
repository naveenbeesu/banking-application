package com.xyz.customer.controller;

import com.xyz.customer.dto.LogonRequest;
import com.xyz.customer.dto.RegistrationRequest;
import com.xyz.customer.dto.RegistrationResponse;
import com.xyz.customer.service.CustomerService;
import com.xyz.customer.service.CustomerServiceImpl;
import com.xyz.customer.validations.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    private final ValidationService validationService;

    public CustomerController(@Qualifier("customerServiceImpl") CustomerServiceImpl customerService, ValidationService validationService) {
        this.customerService = customerService;
        this.validationService = validationService;
    }


    @PostMapping("register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest request) {
        validationService.validateRequest(request);
        RegistrationResponse response = customerService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("logon")
    public ResponseEntity<String> logon(@RequestBody LogonRequest request) {
        String response = customerService.logon(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("upload/{username}")
    public ResponseEntity<String> uploadImage(@NonNull @PathVariable String username, @RequestParam("idDocument") MultipartFile idDocument) throws IOException {
        log.info("username is " + username);
        validationService.validateDocument(idDocument);
        String response = customerService.uploadImage(idDocument, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("download/{username}")
    public ResponseEntity<?> downloadImage(@PathVariable String username) {
        byte[] response = customerService.downloadImage(username);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(response);
    }
}
