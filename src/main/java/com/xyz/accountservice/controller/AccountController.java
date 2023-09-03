package com.xyz.accountservice.controller;

import com.xyz.accountservice.dto.AccountDetails;
import com.xyz.accountservice.dto.TransferRequest;
import com.xyz.accountservice.service.AccountServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("account/")
@Slf4j
public class AccountController {

    private final AccountServiceImpl accountService;

    public AccountController(@Qualifier("accountServiceImpl") AccountServiceImpl accountService) {
        this.accountService = accountService;
    }


    @GetMapping("overview/{accountId}")
    public ResponseEntity<AccountDetails> getOverview(@PathVariable Long accountId) {
        //validationService.validateRequest(request);
        AccountDetails response = accountService.getDetails(accountId);
        return response != null ?  ResponseEntity.ok(response):  ResponseEntity.notFound().build();
    }
    @PostMapping("transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequest request) {
        //validationService.validateRequest(request);
        boolean response = accountService.transfer(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("create/{customerId}")
    public ResponseEntity<String> createAccount(@PathVariable Long customerId) {
        log.info("Called Create endpoint");
        //validationService.validateRequest(request);
        String response = accountService.createAccount(customerId);
        return ResponseEntity.ok(response);
    }
}
