package com.xyz.bankingapplication.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ValidationError {
    String field;
    String message;
}
