package com.xyz.customer.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

import static com.xyz.customer.utils.Constants.*;

@Component
public class DocumentValidator implements ConstraintValidator<ValidDocument, MultipartFile> {

    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        String contentType = multipartFile.getContentType();
        Long documentSize = multipartFile.getSize();
        return isValidContentType(contentType) && isAllowedSize(documentSize);
    }

    private boolean isAllowedSize(Long documentSize) {
        return documentSize <= MAX_DOCUMENT_SIZE && documentSize > MIN_DOCUMENT_SIZE;
    }

    private boolean isValidContentType(String contentType) {
        return Arrays.asList(VALID_CONTENT_TYPES).contains(contentType);
    }
}