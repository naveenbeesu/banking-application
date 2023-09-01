package com.xyz.bankingapplication.controller;

import com.xyz.bankingapplication.dto.LogonRequest;
import com.xyz.bankingapplication.dto.RegistrationRequest;
import com.xyz.bankingapplication.service.CustomerService;
import com.xyz.bankingapplication.service.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(@Qualifier("customerServiceImpl") CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }


    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request){
        String response = customerService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("logon")
    public ResponseEntity<String> logon(@RequestBody LogonRequest request){
        String response = customerService.login(request);
        return ResponseEntity.ok(response);
    }
}
