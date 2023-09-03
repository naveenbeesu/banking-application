package com.xyz.customer.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ValidationError {
    String field;
    String message;
}
