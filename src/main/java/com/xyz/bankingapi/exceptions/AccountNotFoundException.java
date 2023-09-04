package com.xyz.bankingapi.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@RequiredArgsConstructor
public class AccountNotFoundException extends RuntimeException {
    String field;
    String message;

    public AccountNotFoundException(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
