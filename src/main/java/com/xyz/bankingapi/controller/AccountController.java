package com.xyz.bankingapi.controller;

import com.xyz.bankingapi.dto.AccountDetails;
import com.xyz.bankingapi.dto.TransferRequest;
import com.xyz.bankingapi.dto.TransferResponse;
import com.xyz.bankingapi.service.account.AccountServiceImpl;
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


    @GetMapping("overview/{iban}")
    public ResponseEntity<AccountDetails> getOverview(@PathVariable String iban) {
        AccountDetails response = accountService.getAccountDetails(iban);
        return response != null ?  ResponseEntity.ok(response):  ResponseEntity.notFound().build();
    }
    @PutMapping("transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest request) {
        TransferResponse response = accountService.transfer(request);
        return ResponseEntity.ok(response);
    }
}
