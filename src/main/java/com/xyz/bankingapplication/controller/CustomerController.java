package com.xyz.bankingapplication.controller;

import com.xyz.bankingapplication.dto.LogonRequest;
import com.xyz.bankingapplication.dto.RegistrationRequest;
import com.xyz.bankingapplication.dto.RegistrationResponse;
import com.xyz.bankingapplication.service.CustomerService;
import com.xyz.bankingapplication.service.CustomerServiceImpl;
import com.xyz.bankingapplication.validations.ValidDocument;
import com.xyz.bankingapplication.validations.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<RegistrationResponse> register(@RequestPart RegistrationRequest request, @RequestPart("idDocument") MultipartFile idDocument) throws IOException {
        validationService.validateRequest(request);
        RegistrationResponse response = customerService.register(request);
        //response = customerService.uploadImage(idDocument, "nbeesu");
        return ResponseEntity.ok(response);
    }

    @PostMapping("logon")
    public ResponseEntity<String> logon(@RequestBody LogonRequest request){
        String response = customerService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("upload/{username}")
    public ResponseEntity<String> uploadImage(@NonNull @PathVariable String username, @RequestParam("idDocument") MultipartFile idDocument) throws IOException {
        log.info("username is "+ username);
        validationService.validateDocument(idDocument);
        String response = customerService.uploadImage(idDocument, username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("download/{username}")
    public ResponseEntity<?> downloadImage(@PathVariable String username){
        byte[] response = customerService.downloadImage(username);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(response);
    }
}
