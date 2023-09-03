package com.xyz.accountservice.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@RequiredArgsConstructor
public class AccountNotFoundException extends RuntimeException {
    String message;

    public AccountNotFoundException(String message) {
        this.message = message;
    }
}
