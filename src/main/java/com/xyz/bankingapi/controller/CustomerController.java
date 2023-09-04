package com.xyz.bankingapi.controller;

import com.xyz.bankingapi.dto.LogonRequest;
import com.xyz.bankingapi.dto.RegistrationRequest;
import com.xyz.bankingapi.dto.RegistrationResponse;
import com.xyz.bankingapi.service.account.OtpService;
import com.xyz.bankingapi.service.customer.CustomerService;
import com.xyz.bankingapi.service.customer.CustomerServiceImpl;
import com.xyz.bankingapi.validations.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.xyz.bankingapi.utils.Constants.INCORRECT_OTP;

@RestController
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    private final ValidationService validationService;

    private final OtpService otpService;

    public CustomerController(@Qualifier("customerServiceImpl") CustomerServiceImpl customerService, ValidationService validationService, OtpService otpService) {
        this.customerService = customerService;
        this.validationService = validationService;
        this.otpService = otpService;
    }


    @PostMapping("register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest request) {
        //validates input request
        validationService.validateRequest(request);
        RegistrationResponse response = new RegistrationResponse();
        //validates otp
        if (!otpService.validateOtp(request.getMobileNumber(), request.getOtp())) {
            log.info(INCORRECT_OTP);
            response.setStatus(INCORRECT_OTP);
        } else {
            response = customerService.register(request);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("logon")
    public ResponseEntity<String> logon(@RequestBody LogonRequest request) {
        String response = customerService.logon(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("upload/{username}")
    public ResponseEntity<String> uploadImage(@PathVariable String username, @RequestParam("idDocument") MultipartFile idDocument) throws IOException {
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

    @GetMapping("generateOtp/{mobileNumber}")
    public ResponseEntity<String> generateOtp(@PathVariable String mobileNumber) {
        String otp = otpService.sendOtp(mobileNumber);
        return ResponseEntity.ok(otp);
    }
}
