package com.xyz.bankingapi.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static com.xyz.bankingapi.utils.Constants.INVALID_DOCUMENT_TYPE;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {DocumentValidator.class})
@Documented
public @interface ValidDocument {

    String message() default INVALID_DOCUMENT_TYPE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}