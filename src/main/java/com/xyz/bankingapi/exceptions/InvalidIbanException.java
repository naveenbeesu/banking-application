package com.xyz.bankingapi.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@RequiredArgsConstructor
public class InvalidIbanException extends RuntimeException {
    String field;
    String message;

    public InvalidIbanException(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
